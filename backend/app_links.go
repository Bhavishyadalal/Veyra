package main

import (
	"github.com/Bhavishyadalal/Veyra-backend/config"
	"net/http"
)

func handleAppLinks(w http.ResponseWriter, r *http.Request) {
	fingerprints, err := config.AppLinksFingerprints()
	if err != nil || len(fingerprints) == 0 {
		jsonError(w, http.StatusNotFound, "app_links_not_configured", "Android App Links are not configured.")
		return
	}
	w.Header().Set("Cache-Control", "public, max-age=300")
	jsonResponse(w, http.StatusOK, []interface{}{map[string]interface{}{
		"relation": []string{"delegate_permission/common.handle_all_urls"},
		"target":   map[string]interface{}{"namespace": "android_app", "package_name": "app.veyra.music", "sha256_cert_fingerprints": fingerprints},
	}})
}
