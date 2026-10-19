"""Compile real invite/policy sources and run JUnit; Intent/socket behavior is not tested.

Uses the cached Gradle Kotlin compiler. Minimal adapters supply Android Intent's
compile-time shape and the unchanged CODE_LENGTH constant; no network is mocked
or claimed to have been exercised by this parser-only harness.
"""
from pathlib import Path
import os, subprocess, tempfile, sys

root = Path(__file__).resolve().parents[2]
cache = Path(os.environ.get('GRADLE_USER_HOME', str(Path.home() / '.gradle')))
libs = next(cache.glob('wrapper/dists/gradle-8.11.1-bin/*/gradle-8.11.1/lib'))
cp = os.pathsep.join(str(p) for p in sorted(libs.glob('*.jar')))
sources = [
    'shared/src/jvmSharedMain/kotlin/app/veyra/music/data/backend/VeyraBackend.kt',
    'shared/src/commonMain/kotlin/app/veyra/music/data/listentogether/JamInvite.kt',
    'app/src/main/java/app/veyra/music/data/listentogether/JamInviteLink.kt',
    'app/src/test/java/app/veyra/music/BackendMigrationTest.kt',
    'app/src/test/java/app/veyra/music/BackendInviteTest.kt',
]
with tempfile.TemporaryDirectory(prefix='veyra-backend-migration-') as out:
    directory = Path(out)
    (directory/'Intent.kt').write_text('''package android.content
class Intent {
    val action: String? = null
    val dataString: String? = null
    fun getBooleanExtra(name: String, default: Boolean): Boolean = default
    fun putExtra(name: String, value: Boolean) = this
    companion object { const val ACTION_VIEW = "android.intent.action.VIEW" }
}
''')
    (directory/'ListenTogether.kt').write_text('''package app.veyra.music.data.listentogether
object ListenTogether { const val CODE_LENGTH = 6 }
''')
    result = subprocess.run(['java', '-cp', cp, 'org.jetbrains.kotlin.cli.jvm.K2JVMCompiler',
        '-no-stdlib', '-no-reflect', '-classpath', cp, '-d', out,
        *[str(root/p) for p in sources], str(directory/'Intent.kt'), str(directory/'ListenTogether.kt')])
    if result.returncode: sys.exit(result.returncode)
    result = subprocess.run(['java', '-cp', out+os.pathsep+cp, 'org.junit.runner.JUnitCore',
        'app.veyra.music.BackendMigrationTest', 'app.veyra.music.BackendInviteTest'])
    if result.returncode: sys.exit(result.returncode)
