package main

import (
	"bytes"
	"encoding/json"
	"net/http"
	"strings"
	"testing"
)

// Guests supply no account credential. A server-issued membership token still
// authorizes every private party request after creation or joining.
func TestGuestCreateJoinAndTokenAuthorization(t *testing.T) {
	server := setupTestServer()
	defer server.Close()
	type membership struct {
		Code  string `json:"code"`
		Token string `json:"token"`
		You   struct {
			DisplayName string `json:"displayName"`
		} `json:"you"`
	}
	post := func(path, user, device, name string, expected int) membership {
		t.Helper()
		payload, err := json.Marshal(map[string]string{
			"userId": user, "deviceId": device, "displayName": name,
		})
		if err != nil {
			t.Fatal(err)
		}
		response, err := http.Post(server.URL+path, "application/json", bytes.NewReader(payload))
		if err != nil {
			t.Fatal(err)
		}
		defer response.Body.Close()
		if response.StatusCode != expected {
			t.Fatalf("%s: got %d, want %d", path, response.StatusCode, expected)
		}
		var result membership
		if err := json.NewDecoder(response.Body).Decode(&result); err != nil {
			t.Fatal(err)
		}
		if result.Code == "" || result.Token == "" {
			t.Fatal("guest did not receive a membership token")
		}
		if result.You.DisplayName != name {
			t.Fatal("guest display name was not retained")
		}
		return result
	}
	host := post("/api/parties", "guest_"+strings.Repeat("a", 32), "install-a", "Guest A001", http.StatusCreated)
	defer store.Drop(host.Code)
	guest := post("/api/parties/"+host.Code+"/join", "guest_"+strings.Repeat("b", 32), "install-b", "🎵 Listener", http.StatusOK)
	if host.Token == guest.Token {
		t.Fatal("members must have separate tokens")
	}
	for _, check := range []struct {
		token  string
		status int
	}{
		{"", http.StatusUnauthorized}, {"invalid-token", http.StatusUnauthorized},
		{host.Token, http.StatusOK}, {guest.Token, http.StatusOK},
	} {
		request, err := http.NewRequest(http.MethodGet, server.URL+"/api/parties/"+host.Code, nil)
		if err != nil {
			t.Fatal(err)
		}
		if check.token != "" {
			request.Header.Set("Authorization", "Bearer "+check.token)
		}
		response, err := http.DefaultClient.Do(request)
		if err != nil {
			t.Fatal(err)
		}
		response.Body.Close()
		if response.StatusCode != check.status {
			t.Fatalf("guest authorization: got %d, want %d", response.StatusCode, check.status)
		}
	}
}
