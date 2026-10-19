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
files = [
    "app/src/main/java/app/veyra/music/ui/screens/" + name
    for name in ["SettingsSheet.kt", "AccountAndScrobblingScreen.kt", "DiscordScreen.kt",
                 "EqualizerScreen.kt", "ListenTogetherScreen.kt", "SourcesScreen.kt"]
]
files += ["app/src/main/java/app/veyra/music/ui/components/" + name for name in [
    "AccountAlerts.kt", "AccountChannelDialog.kt", "AppLanguageDialog.kt",
    "LyricsSourcesDialog.kt", "TranslationLanguageDialog.kt", "UpdateAvailableDialog.kt"]]
files += ["sharedUi/src/jvmSharedMain/kotlin/app/veyra/music/ui/components/Common.kt"]
with tempfile.TemporaryDirectory(prefix="veyra-action-audit-") as target:
    subprocess.run([str(java_home / "javac"), "-cp", classpath, "-d", target,
                    str(Path(__file__).with_name("SettingsActionAudit.java"))], check=True)
    subprocess.run([str(java_home / "java"), "-cp", target + os.pathsep + classpath,
                    "SettingsActionAudit", *files], cwd=root, check=True)
# Authentication, preference persistence, and launcher application logic must remain unchanged.
for relative in ["app/src/main/java/app/veyra/music/MainActivity.kt",
                 "app/src/main/java/app/veyra/music/AppIcon.kt",
                 "app/src/main/java/app/veyra/music/data/settings/AppSettings.kt"]:
    baseline = subprocess.check_output(["git", "show", "8a03bb7:" + relative], cwd=root)
    assert baseline == (root / relative).read_bytes(), relative
for path in (root / "app/src/main/java/app/veyra/music/auth").glob("*.kt"):
    baseline = subprocess.check_output(["git", "show", "8a03bb7:" + str(path.relative_to(root))], cwd=root)
    assert baseline == path.read_bytes(), str(path)
print("PASS: auth source files, MainActivity routing, AppSettings persistence and AppIcon apply/rollback are unchanged.")
