package config

import (
	"encoding/hex"
	"errors"
	"os"
	"strings"
)

// Only public signing-certificate fingerprints belong here, never private keys.
func AppLinksFingerprints() ([]string, error) {
	raw := strings.TrimSpace(os.Getenv("ANDROID_APP_SHA256_CERT_FINGERPRINTS"))
	if raw == "" {
		return nil, nil
	}
	var result []string
	seen := map[string]bool{}
	for _, value := range strings.Split(raw, ",") {
		decoded, err := hex.DecodeString(strings.ReplaceAll(strings.TrimSpace(value), ":", ""))
		if err != nil || len(decoded) != 32 {
			return nil, errors.New("expected comma-separated SHA-256 certificate fingerprints")
		}
		encoded := strings.ToUpper(hex.EncodeToString(decoded))
		pairs := make([]string, 32)
		for i := range pairs {
			pairs[i] = encoded[i*2 : i*2+2]
		}
		normalized := strings.Join(pairs, ":")
		if !seen[normalized] {
			result = append(result, normalized)
			seen[normalized] = true
		}
	}
	return result, nil
}
