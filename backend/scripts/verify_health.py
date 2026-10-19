"""Validate the public health response without creating a room or membership."""
import json
from pathlib import Path
import sys


def verify(payload):
    if not isinstance(payload, dict):
        raise ValueError("Health response is not a JSON object")
    expected = {"ok": True, "service": "veyra-listen-together", "protocol": "veyra.v1"}
    for key, value in expected.items():
        if payload.get(key) != value or (key == "ok" and payload.get(key) is not True):
            raise ValueError(f"Unexpected Veyra health field: {key}")
    return payload.get("version", "unknown")


if __name__ == "__main__":
    try:
        version = verify(json.loads(Path(sys.argv[1]).read_text()))
    except (OSError, ValueError, IndexError) as error:
        sys.exit(f"Veyra health check failed: {error}")
    print(f"Veyra service is healthy (version {version}).")
