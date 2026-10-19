#!/usr/bin/env python3
"""Check official Android endpoints and retirement guards in project sources."""
from pathlib import Path
import re

root = Path(__file__).resolve().parents[1]
origin = "https://veyra-listen-together.onrender.com"
host = origin.removeprefix("https://")
policy = "shared/src/jvmSharedMain/kotlin/app/veyra/music/data/backend/VeyraBackend.kt"
required = {
    "app/build.gradle.kts": [f'?: "{origin}"'],
    "app/src/main/AndroidManifest.xml": [f'android:host="{host}"'],
    policy: [f'const val ORIGIN = "{origin}"', 'const val PRESENCE_ENDPOINT = "$ORIGIN/api/presence"'],
    "shared/src/commonMain/kotlin/app/veyra/music/data/listentogether/JamInvite.kt": [f'const val ORIGIN = "{origin}"'],
    "shared/src/jvmSharedMain/kotlin/app/veyra/music/data/presence/Presence.kt": ['const val ENDPOINT = VeyraBackend.PRESENCE_ENDPOINT'],
    "app/src/main/java/app/veyra/music/data/listentogether/JamInviteLink.kt": ['const val ORIGIN = VeyraBackend.ORIGIN', 'VeyraBackend.HOST'],
    "app/src/main/java/app/veyra/music/data/listentogether/ListenTogether.kt": [
        'VeyraBackend.migrateSavedServer(rawSaved)', 'VeyraBackend.discardSavedMembership(',
        'VeyraBackend.requireActive(raw)', 'VeyraBackend.isRetired(serverBase)',
    ],
}
for rel, markers in required.items():
    text = (root / rel).read_text()
    for marker in markers:
        if marker not in text:
            raise SystemExit(f"Missing backend integration in {rel}: {marker}")

# Retired host literals are allowed only in the explicit retirement policy.
retired = re.compile(r'(?:[\w.-]*bitchord[\w.-]*\.onrender\.com|(?:api\.)?bitchord\.kushagrasingh\.in)', re.I)
production = [root / 'app/build.gradle.kts', *(root / 'app/src/main').rglob('*.kt'),
              *(root / 'app/src/main').rglob('*.xml')]
for folder in ('shared/src/commonMain', 'shared/src/jvmSharedMain', 'sharedUi/src'):
    production.extend(p for p in (root / folder).rglob('*.kt') if not any('Test' in part for part in p.parts))
for path in production:
    if path.relative_to(root).as_posix() == policy:
        continue
    if retired.search(path.read_text()):
        raise SystemExit(f"Retired operator endpoint in Android production source: {path.relative_to(root)}")
print(f"Android backend audit passed: {origin}; migration and token guards present")
