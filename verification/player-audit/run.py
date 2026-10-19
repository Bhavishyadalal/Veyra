"""Run from any directory with JDK 17 and the root Gradle wrapper already bootstrapped."""
from pathlib import Path
import os
import subprocess
import tempfile

root = Path(__file__).resolve().parents[2]
cache = Path(os.environ.get("GRADLE_USER_HOME", str(Path.home() / ".gradle")))
libs = next(cache.glob("wrapper/dists/gradle-8.11.1-bin/*/gradle-8.11.1/lib"))
classpath = os.pathsep.join(str(p) for p in libs.glob("*.jar"))
java_home = Path(os.environ["JAVA_HOME"]) / "bin"
player = "sharedUi/src/jvmSharedMain/kotlin/app/veyra/music/ui/player/"
files = [player + name for name in ["PlayerControls.kt", "NowPlayingScreen.kt", "ThinSlider.kt",
                                   "PlayerQueue.kt", "PlayerState.kt", "LandscapePlayer.kt"]]
with tempfile.TemporaryDirectory(prefix="veyra-action-audit-") as target:
    subprocess.run([str(java_home / "javac"), "-cp", classpath, "-d", target,
                    str(Path(__file__).with_name("PlayerActionAudit.java"))], check=True)
    subprocess.run([str(java_home / "java"), "-cp", target + os.pathsep + classpath,
                    "PlayerActionAudit", *files], cwd=root, check=True)
# Host callbacks, authentication and playback engines are outside this presentation change.
baseline = "3bfcbcd89bca544e71fb4b17df5433d444737a82"
unchanged = ["app/src/main/java/app/veyra/music/MainActivity.kt",
             "app/src/main/java/app/veyra/music/data/settings/AppSettings.kt",
             player + "LandscapePlayer.kt"]
for folder in ["shared/src", "app/src/main/java/app/veyra/music/playback", "app/src/main/java/app/veyra/music/auth"]:
    unchanged += subprocess.check_output(["git", "ls-files", folder], cwd=root, text=True).splitlines()
# Every tracked Android player host is also compared, regardless of its directory.
unchanged += [p for p in subprocess.check_output(["git", "ls-files"], cwd=root, text=True).splitlines()
              if p.endswith(("AndroidPlayerHost.kt", "PlayerSheetMotion.kt", "CanvasArtworkPlayer.kt"))]
for relative in set(unchanged):
    original = subprocess.check_output(["git", "show", baseline + ":" + relative], cwd=root)
    assert original == (root / relative).read_bytes(), relative
print(f"PASS: {len(set(unchanged))} playback/shared/auth/host source files are byte-identical to preview baseline.")
