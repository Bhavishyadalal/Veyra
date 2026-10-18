"""Run real, Android-independent regression tests using the pinned Gradle compiler.

Bootstrap with ./gradlew --version first. This does not validate Compose compilation.
"""
from pathlib import Path
import os
import subprocess
import tempfile
import sys

root = Path(__file__).resolve().parents[1]
cache = Path(os.environ.get("GRADLE_USER_HOME", str(Path.home() / ".gradle")))
libraries = list(cache.glob("wrapper/dists/gradle-8.11.1-bin/*/gradle-8.11.1/lib"))
if not libraries:
    sys.exit("Run ./gradlew --version first, with the same GRADLE_USER_HOME.")
classpath = os.pathsep.join(str(path) for path in sorted(libraries[0].glob("*.jar")))
files = [
    "app/src/main/java/app/veyra/music/data/artwork/ExploreArtworkCache.kt",
    "app/src/test/java/app/veyra/music/ExploreArtworkCacheTest.kt",
    "app/src/main/java/app/veyra/music/playback/QueueUrlWarmup.kt",
    "app/src/test/java/app/veyra/music/QueueUrlWarmupTest.kt",
    "app/src/main/java/app/veyra/music/ui/player/CanvasRenderPolicy.kt",
    "app/src/test/java/app/veyra/music/CanvasRenderPolicyTest.kt",
    "app/src/main/java/app/veyra/music/data/listentogether/GuestPartyIdentity.kt",
    "app/src/test/java/app/veyra/music/GuestPartyIdentityTest.kt",
    "app/src/main/java/app/veyra/music/ui/components/BottomNavigationGeometry.kt",
    "app/src/test/java/app/veyra/music/BottomNavigationGeometryTest.kt",
    "app/src/main/java/app/veyra/music/ui/screens/SettingsSearch.kt",
    "app/src/test/java/app/veyra/music/SettingsSearchTest.kt",
]
with tempfile.TemporaryDirectory(prefix="veyra-tests-") as output:
    compile_result = subprocess.run([
        "java", "-cp", classpath, "org.jetbrains.kotlin.cli.jvm.K2JVMCompiler",
        "-no-stdlib", "-no-reflect", "-classpath", classpath, "-d", output,
        *(str(root / name) for name in files),
    ])
    if compile_result.returncode:
        sys.exit(compile_result.returncode)
    result = subprocess.run([
        "java", "-cp", output + os.pathsep + classpath, "org.junit.runner.JUnitCore",
        "app.veyra.music.ExploreArtworkCacheTest",
        "app.veyra.music.QueueUrlWarmupTest",
        "app.veyra.music.CanvasRenderPolicyTest",
        "app.veyra.music.GuestPartyIdentityTest", "app.veyra.music.SettingsSearchTest",
        "app.veyra.music.BottomNavigationGeometryTest",
    ])
    sys.exit(result.returncode)
