package main

import (
	"bytes"
	"encoding/json"
	"github.com/Bhavishyadalal/Veyra-backend/config"
	"github.com/gorilla/websocket"
	"io"
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"
)

func TestVeyraIdentityAndAppLinks(t *testing.T) {
	ts := setupTestServer()
	defer ts.Close()
	for _, path := range []string{"/", "/healthz"} {
		res, err := http.Get(ts.URL + path)
		if err != nil {
			t.Fatal(err)
		}
		var body map[string]interface{}
		err = json.NewDecoder(res.Body).Decode(&body)
		res.Body.Close()
		if err != nil || body["service"] != serviceName || body["version"] != "0.1.5" || body["protocol"] != "veyra.v1" {
			t.Fatalf("wrong identity: %v (%v)", body, err)
		}
	}
	t.Setenv("ANDROID_APP_SHA256_CERT_FINGERPRINTS", "")
	res, err := http.Get(ts.URL + "/.well-known/assetlinks.json")
	if err != nil {
		t.Fatal(err)
	}
	res.Body.Close()
	if res.StatusCode != http.StatusNotFound {
		t.Fatal("unset certificate must not advertise fake App Links")
	}
	t.Setenv("ANDROID_APP_SHA256_CERT_FINGERPRINTS", strings.Repeat("ab", 32))
	res, err = http.Get(ts.URL + "/.well-known/assetlinks.json")
	if err != nil {
		t.Fatal(err)
	}
	defer res.Body.Close()
	var body []struct {
		Target struct {
			Package      string   `json:"package_name"`
			Fingerprints []string `json:"sha256_cert_fingerprints"`
		} `json:"target"`
	}
	if err := json.NewDecoder(res.Body).Decode(&body); err != nil {
		t.Fatal(err)
	}
	if res.StatusCode != 200 || len(body) != 1 || body[0].Target.Package != "app.veyra.music" || len(body[0].Target.Fingerprints) != 1 {
		t.Fatalf("wrong association: %v", body)
	}
}

func TestDeployedPreviewRouteAndPrivateDataBoundary(t *testing.T) {
	ts := setupTestServer()
	defer ts.Close()
	res, err := http.Post(ts.URL+"/api/parties", "application/json", strings.NewReader(`{"userId":"guest_a","deviceId":"preview-install","displayName":"Veyra listener"}`))
	if err != nil {
		t.Fatal(err)
	}
	var joined map[string]interface{}
	err = json.NewDecoder(res.Body).Decode(&joined)
	res.Body.Close()
	if err != nil {
		t.Fatal(err)
	}
	code, ok := joined["code"].(string)
	if !ok {
		t.Fatalf("no party: %v", joined)
	}
	defer store.Drop(code)
	res, err = http.Get(ts.URL + "/api/parties/" + code + "/preview")
	if err != nil {
		t.Fatal(err)
	}
	defer res.Body.Close()
	var preview map[string]interface{}
	if err := json.NewDecoder(res.Body).Decode(&preview); err != nil {
		t.Fatal(err)
	}
	if res.StatusCode != 200 || preview["hostName"] != "Veyra listener" {
		t.Fatalf("missing preview route: %v", preview)
	}
	encoded, _ := json.Marshal(preview)
	for _, field := range []string{"token", "queue", "playback", "deviceId", "userId", "memberId"} {
		if bytes.Contains(encoded, []byte(`"`+field+`"`)) {
			t.Fatalf("public preview exposed %s", field)
		}
	}
}

func TestReplacementSocketCannotBeMarkedDisconnectedByOldSocket(t *testing.T) {
	createLimiter = newIPRateLimiter(time.Minute, config.CreateRatePerMinute, config.RateLimitMaxEntries)
	finished := make(chan struct{}, 4)
	handler := newHTTPHandler()
	ts := httptest.NewServer(http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		handler.ServeHTTP(w, r)
		if strings.HasPrefix(r.URL.Path, "/ws/parties/") {
			finished <- struct{}{}
		}
	}))
	defer ts.Close()
	res, err := http.Post(ts.URL+"/api/parties", "application/json", strings.NewReader(`{"userId":"guest_a","deviceId":"reconnect-install","displayName":"Veyra reconnect"}`))
	if err != nil {
		t.Fatal(err)
	}
	var member struct {
		Code, Token string
		You         struct {
			MemberID string `json:"memberId"`
		}
	}
	err = json.NewDecoder(res.Body).Decode(&member)
	res.Body.Close()
	if err != nil {
		t.Fatal(err)
	}
	defer store.Drop(member.Code)
	connect := func() *websocket.Conn {
		t.Helper()
		header := http.Header{}
		header.Set("Authorization", "Bearer "+member.Token)
		ws, _, err := websocket.DefaultDialer.Dial("ws"+strings.TrimPrefix(ts.URL, "http")+"/ws/parties/"+member.Code, header)
		if err != nil {
			t.Fatal(err)
		}
		_ = ws.SetReadDeadline(time.Now().Add(3 * time.Second))
		var welcome map[string]interface{}
		if err := ws.ReadJSON(&welcome); err != nil || welcome["type"] != "welcome" {
			ws.Close()
			t.Fatalf("missing welcome: %v", err)
		}
		return ws
	}
	first := connect()
	defer first.Close()
	replacement := connect()
	defer func() {
		replacement.Close()
		select {
		case <-finished:
		case <-time.After(3 * time.Second):
			t.Error("replacement did not finish")
		}
	}()
	select {
	case <-finished:
	case <-time.After(3 * time.Second):
		t.Fatal("outgoing handler did not finish")
	}
	p, err := store.Get(member.Code)
	if err != nil {
		t.Fatal(err)
	}
	p.Lock()
	m := p.Members[member.You.MemberID]
	connected := m != nil && m.Connected
	count := len(p.Members)
	p.Unlock()
	if !connected || count != 1 {
		t.Fatal("old socket cleanup disconnected replacement or duplicated membership")
	}
	if err := replacement.WriteJSON(map[string]interface{}{"type": "ping", "clientMs": 123}); err != nil {
		t.Fatal(err)
	}
	for {
		var frame map[string]interface{}
		if err := replacement.ReadJSON(&frame); err != nil {
			t.Fatal(err)
		}
		if frame["type"] == "pong" {
			if frame["clientMs"] != float64(123) {
				t.Fatal("wrong pong")
			}
			break
		}
	}
}

func TestDisallowedBrowserOriginCannotJoinSocket(t *testing.T) {
	ts := setupTestServer()
	defer ts.Close()
	res, err := http.Post(ts.URL+"/api/parties", "application/json", strings.NewReader(`{"userId":"guest_a","deviceId":"origin-install","displayName":"Origin test"}`))
	if err != nil {
		t.Fatal(err)
	}
	var member struct{ Code, Token string }
	err = json.NewDecoder(res.Body).Decode(&member)
	res.Body.Close()
	if err != nil {
		t.Fatal(err)
	}
	defer store.Drop(member.Code)
	header := http.Header{"Authorization": []string{"Bearer " + member.Token}, "Origin": []string{"https://untrusted.invalid"}}
	conn, denied, err := websocket.DefaultDialer.Dial("ws"+strings.TrimPrefix(ts.URL, "http")+"/ws/parties/"+member.Code, header)
	if conn != nil {
		conn.Close()
	}
	if denied != nil {
		io.Copy(io.Discard, denied.Body)
		denied.Body.Close()
	}
	if err == nil || denied == nil || denied.StatusCode != 403 {
		t.Fatal("unlisted browser origin was not rejected")
	}
}

func TestLeaveRevokesTokenAndClosesLiveSocket(t *testing.T) {
	ts := setupTestServer()
	defer ts.Close()
	res, err := http.Post(ts.URL+"/api/parties", "application/json", strings.NewReader(`{"userId":"guest_a","deviceId":"leave-install","displayName":"Leaving listener"}`))
	if err != nil {
		t.Fatal(err)
	}
	var member struct{ Code, Token string }
	err = json.NewDecoder(res.Body).Decode(&member)
	res.Body.Close()
	if err != nil {
		t.Fatal(err)
	}
	defer store.Drop(member.Code)
	header := http.Header{}
	header.Set("Authorization", "Bearer "+member.Token)
	ws, _, err := websocket.DefaultDialer.Dial("ws"+strings.TrimPrefix(ts.URL, "http")+"/ws/parties/"+member.Code, header)
	if err != nil {
		t.Fatal(err)
	}
	defer ws.Close()
	_ = ws.SetReadDeadline(time.Now().Add(2 * time.Second))
	var welcome map[string]interface{}
	if err = ws.ReadJSON(&welcome); err != nil {
		t.Fatal(err)
	}
	req, err := http.NewRequest(http.MethodPost, ts.URL+"/api/parties/"+member.Code+"/leave", nil)
	if err != nil {
		t.Fatal(err)
	}
	req.Header.Set("Authorization", "Bearer "+member.Token)
	res, err = http.DefaultClient.Do(req)
	if err != nil {
		t.Fatal(err)
	}
	res.Body.Close()
	if res.StatusCode != 200 {
		t.Fatal("leave failed")
	}
	req, err = http.NewRequest(http.MethodGet, ts.URL+"/api/parties/"+member.Code, nil)
	if err != nil {
		t.Fatal(err)
	}
	req.Header.Set("Authorization", "Bearer "+member.Token)
	res, err = http.DefaultClient.Do(req)
	if err != nil {
		t.Fatal(err)
	}
	res.Body.Close()
	if res.StatusCode != 401 {
		t.Fatal("left member token still authorized")
	}
	seenBye := false
	for i := 0; i < 10; i++ {
		var value map[string]interface{}
		err := ws.ReadJSON(&value)
		if err != nil {
			if timeout, ok := err.(interface{ Timeout() bool }); ok && timeout.Timeout() {
				t.Fatal("left socket remained open")
			}
			if !seenBye {
				t.Fatal("socket closed without left reason")
			}
			return
		}
		if value["type"] == "bye" && value["reason"] == "left" {
			seenBye = true
		}
	}
	t.Fatal("left socket did not close")
}

func TestCreationUsesConfiguredDefaultCapacity(t *testing.T) {
	old := config.MaxMembers
	config.MaxMembers = 3
	defer func() { config.MaxMembers = old }()
	ts := setupTestServer()
	defer ts.Close()
	res, err := http.Post(ts.URL+"/api/parties", "application/json", strings.NewReader(`{"userId":"guest_a","deviceId":"capacity-install","displayName":"Capacity test"}`))
	if err != nil {
		t.Fatal(err)
	}
	var member struct {
		Code  string
		Party struct {
			MaxMembers int `json:"maxMembers"`
		}
	}
	err = json.NewDecoder(res.Body).Decode(&member)
	res.Body.Close()
	if err != nil {
		t.Fatal(err)
	}
	defer store.Drop(member.Code)
	if res.StatusCode != 201 || member.Party.MaxMembers != 3 {
		t.Fatal("configured room capacity was ignored")
	}
}
