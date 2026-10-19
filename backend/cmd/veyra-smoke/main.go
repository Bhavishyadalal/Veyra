// veyra-smoke creates one temporary room and drives two actual WebSocket clients.
package main

import (
	"bytes"
	"crypto/rand"
	"encoding/hex"
	"encoding/json"
	"errors"
	"flag"
	"fmt"
	"github.com/gorilla/websocket"
	"io"
	"net/http"
	"net/url"
	"os"
	"strings"
	"time"
)

type object = map[string]interface{}
type probe struct {
	base   string
	client *http.Client
	checks int
}

func (p *probe) pass(label string) { p.checks++; fmt.Println("PASS", label) }
func (p *probe) request(method, path, token string, body interface{}, expected int) (object, error) {
	var reader io.Reader
	if body != nil {
		data, err := json.Marshal(body)
		if err != nil {
			return nil, err
		}
		reader = bytes.NewReader(data)
	}
	req, err := http.NewRequest(method, p.base+path, reader)
	if err != nil {
		return nil, err
	}
	if body != nil {
		req.Header.Set("Content-Type", "application/json")
	}
	if token != "" {
		req.Header.Set("Authorization", "Bearer "+token)
	}
	res, err := p.client.Do(req)
	if err != nil {
		return nil, err
	}
	defer res.Body.Close()
	if res.StatusCode != expected {
		return nil, fmt.Errorf("%s %s returned HTTP %d; wanted %d", method, path, res.StatusCode, expected)
	}
	var result object
	if expected == http.StatusNoContent {
		return result, nil
	}
	err = json.NewDecoder(io.LimitReader(res.Body, 1024*1024)).Decode(&result)
	return result, err
}
func frame(ws *websocket.Conn, kind string, accept func(object) bool) (object, error) {
	_ = ws.SetReadDeadline(time.Now().Add(12 * time.Second))
	for i := 0; i < 100; i++ {
		var value object
		if err := ws.ReadJSON(&value); err != nil {
			return nil, err
		}
		if value["type"] == kind && (accept == nil || accept(value)) {
			return value, nil
		}
		if value["type"] == "error" {
			return nil, fmt.Errorf("unexpected protocol error: %v", value["error"])
		}
	}
	return nil, errors.New("expected frame did not arrive")
}
func payload(f object, key string) object {
	result, _ := f[key].(map[string]interface{})
	return result
}
func (p *probe) connect(code, token string) (*websocket.Conn, error) {
	u, err := url.Parse(p.base)
	if err != nil {
		return nil, err
	}
	if u.Scheme == "https" {
		u.Scheme = "wss"
	} else {
		u.Scheme = "ws"
	}
	u.Path = "/ws/parties/" + url.PathEscape(code)
	headers := http.Header{}
	headers.Set("Authorization", "Bearer "+token)
	dialer := websocket.Dialer{HandshakeTimeout: 20 * time.Second}
	ws, _, err := dialer.Dial(u.String(), headers)
	if err != nil {
		return nil, err
	}
	if _, err = frame(ws, "welcome", nil); err != nil {
		ws.Close()
		return nil, err
	}
	return ws, nil
}
func control(ws *websocket.Conn, action string, fields object) error {
	fields["type"] = "control"
	fields["action"] = action
	return ws.WriteJSON(fields)
}
func run(raw string) error {
	u, err := url.Parse(strings.TrimRight(raw, "/"))
	if err != nil {
		return err
	}
	if (u.Scheme != "https" && u.Scheme != "http") || u.Host == "" || u.User != nil || u.Path != "" || u.RawQuery != "" || u.Fragment != "" {
		return errors.New("server must be an HTTP(S) origin without credentials, path, query or fragment")
	}
	if u.Scheme == "http" && u.Hostname() != "localhost" && u.Hostname() != "127.0.0.1" && u.Hostname() != "::1" {
		return errors.New("public deployment checks require HTTPS")
	}
	p := probe{base: u.String(), client: &http.Client{Timeout: 90 * time.Second, CheckRedirect: func(*http.Request, []*http.Request) error { return http.ErrUseLastResponse }}}
	health, err := p.request("GET", "/healthz", "", nil, 200)
	if err != nil {
		return err
	}
	if health["ok"] != true || health["service"] != "veyra-listen-together" || health["version"] != "0.1.5" || health["protocol"] != "veyra.v1" {
		return errors.New("deployment does not identify as Veyra backend 0.1.5 / veyra.v1")
	}
	p.pass("Veyra deployment identity and health")
	clock, err := p.request("GET", "/api/time", "", nil, 200)
	if err != nil {
		return err
	}
	if value, ok := clock["serverMs"].(float64); !ok || value <= 0 {
		return errors.New("invalid server clock")
	}
	p.pass("server clock")
	random := make([]byte, 16)
	if _, err := rand.Read(random); err != nil {
		return err
	}
	id := hex.EncodeToString(random)
	uuid := id[:8] + "-" + id[8:12] + "-" + id[12:16] + "-" + id[16:20] + "-" + id[20:]
	identity := func(suffix string) object {
		return object{"userId": "guest_probe_" + id + suffix, "deviceId": "probe_" + id + suffix, "displayName": "Veyra probe " + suffix}
	}
	host, err := p.request("POST", "/api/parties", "", identity("host"), 201)
	if err != nil {
		return err
	}
	code, _ := host["code"].(string)
	hostToken, _ := host["token"].(string)
	if code == "" || hostToken == "" {
		return errors.New("host membership missing")
	}
	path := "/api/parties/" + url.PathEscape(code)
	defer p.request("POST", path+"/leave", hostToken, nil, 200)
	guest, err := p.request("POST", path+"/join", "", identity("guest"), 200)
	if err != nil {
		return err
	}
	guestToken, _ := guest["token"].(string)
	defer p.request("POST", path+"/leave", guestToken, nil, 200)
	if guestToken == "" || guestToken == hostToken {
		return errors.New("guest membership tokens are not distinct")
	}
	p.pass("guest create/join without Google credentials")
	if _, err = p.request("GET", path, "invalid", nil, 401); err != nil {
		return err
	}
	p.pass("private snapshot rejects invalid token")
	preview, err := p.request("GET", path+"/preview", "", nil, 200)
	if err != nil {
		return err
	}
	if preview["memberCount"] != float64(2) {
		return errors.New("public preview has wrong member count")
	}
	p.pass("public invite preview")
	a, err := p.connect(code, hostToken)
	if err != nil {
		return err
	}
	defer a.Close()
	b, err := p.connect(code, guestToken)
	if err != nil {
		return err
	}
	defer b.Close()
	p.pass("two authenticated WebSocket welcome frames")
	if err = a.WriteJSON(object{"type": "ping", "clientMs": 12345}); err != nil {
		return err
	}
	pong, err := frame(a, "pong", nil)
	if err != nil {
		return err
	}
	if pong["clientMs"] != float64(12345) {
		return errors.New("clock echo mismatch")
	}
	p.pass("WebSocket ping/pong")
	tracks := []object{{"videoId": "veyra_probe_a", "title": "Probe A", "artist": "Veyra", "durationMs": 180000}, {"videoId": "veyra_probe_b", "title": "Probe B", "artist": "Veyra"}, {"videoId": "veyra_probe_c", "title": "Probe C", "artist": "Veyra"}}
	if err = control(a, "setQueue", object{"queue": tracks, "queueIndex": 0}); err != nil {
		return err
	}
	for _, ws := range []*websocket.Conn{a, b} {
		q, err := frame(ws, "queue", nil)
		if err != nil {
			return err
		}
		items, _ := payload(q, "queue")["items"].([]interface{})
		if len(items) != 3 {
			return errors.New("queue did not reach both clients")
		}
	}
	p.pass("shared queue broadcast to both clients")
	if err = control(a, "setTrack", object{"track": tracks[0], "queueIndex": 0, "positionMs": 12000, "isPlaying": false}); err != nil {
		return err
	}
	for _, ws := range []*websocket.Conn{a, b} {
		_, err = frame(ws, "state", func(f object) bool { return payload(payload(f, "playback"), "track")["videoId"] == "veyra_probe_a" })
		if err != nil {
			return err
		}
	}
	p.pass("track and anchored state synchronization")
	if err = control(a, "queueMove", object{"fromIndex": 2, "toIndex": 1, "videoId": "veyra_probe_c"}); err != nil {
		return err
	}
	for _, ws := range []*websocket.Conn{a, b} {
		_, err = frame(ws, "queue", func(f object) bool {
			items, _ := payload(f, "queue")["items"].([]interface{})
			if len(items) != 3 {
				return false
			}
			moved, ok := items[1].(map[string]interface{})
			return ok && moved["videoId"] == "veyra_probe_c"
		})
		if err != nil {
			return err
		}
	}
	p.pass("queue reorder delta reaches both clients")
	if err = control(a, "setHostOnlyControl", object{"enabled": true}); err != nil {
		return err
	}
	for _, ws := range []*websocket.Conn{a, b} {
		_, err = frame(ws, "members", func(f object) bool { return f["hostOnlyControl"] == true })
		if err != nil {
			return err
		}
	}
	if err = control(b, "seek", object{"positionMs": 5000}); err != nil {
		return err
	}
	denied, err := frame(b, "error", nil)
	if err != nil {
		return err
	}
	if denied["error"] != "host_only" {
		return errors.New("guest bypassed host-only lock")
	}
	p.pass("host-only permission enforced by server")
	if err = control(a, "seek", object{"positionMs": 23000}); err != nil {
		return err
	}
	var sequence interface{}
	for _, ws := range []*websocket.Conn{a, b} {
		state, err := frame(ws, "state", func(f object) bool { return payload(f, "playback")["positionMs"] == float64(23000) })
		if err != nil {
			return err
		}
		next := payload(state, "playback")["seq"]
		if sequence != nil && sequence != next {
			return errors.New("clients received different playback sequence")
		}
		sequence = next
	}
	p.pass("host seek and monotonic playback sequence agree")
	if err = b.WriteJSON(object{"type": "syncQueue"}); err != nil {
		return err
	}
	if _, err = frame(b, "queue", nil); err != nil {
		return err
	}
	p.pass("queue resynchronization")
	if _, err = p.request("POST", "/api/presence", "", object{"id": uuid, "platform": "android", "open": true}, 200); err != nil {
		return err
	}
	if _, err = p.request("POST", "/api/presence", "", object{"id": uuid, "platform": "android", "open": false}, 200); err != nil {
		return err
	}
	p.pass("Veyra presence open/close")
	a.Close()
	b.Close()
	if _, err = p.request("POST", path+"/leave", guestToken, nil, 200); err != nil {
		return err
	}
	if _, err = p.request("POST", path+"/leave", hostToken, nil, 200); err != nil {
		return err
	}
	p.pass("test memberships leave cleanly")
	fmt.Printf("%d deployed-service checks passed. This tests synchronization metadata, not Android audio playback.\n", p.checks)
	return nil
}
func main() {
	server := flag.String("server", "http://127.0.0.1:8000", "Veyra backend origin")
	flag.Parse()
	if err := run(*server); err != nil {
		fmt.Fprintln(os.Stderr, "FAIL:", err)
		os.Exit(1)
	}
}
