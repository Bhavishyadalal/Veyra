package config

import (
	"strings"
	"testing"
)

func TestAppLinksFingerprints(t *testing.T) {
	t.Setenv("ANDROID_APP_SHA256_CERT_FINGERPRINTS", "")
	if got, err := AppLinksFingerprints(); err != nil || len(got) != 0 {
		t.Fatalf("unset: %v %v", got, err)
	}
	plain := strings.Repeat("ab", 32)
	colon := strings.TrimSuffix(strings.Repeat("AB:", 32), ":")
	t.Setenv("ANDROID_APP_SHA256_CERT_FINGERPRINTS", plain+", "+colon)
	got, err := AppLinksFingerprints()
	if err != nil || len(got) != 1 || got[0] != colon {
		t.Fatalf("normalization: %v %v", got, err)
	}
	for _, bad := range []string{"not-a-certificate", strings.Repeat("aa", 31), plain + ",", plain + ",bad"} {
		t.Setenv("ANDROID_APP_SHA256_CERT_FINGERPRINTS", bad)
		if _, err := AppLinksFingerprints(); err == nil {
			t.Fatal("accepted invalid certificate")
		}
	}
}
