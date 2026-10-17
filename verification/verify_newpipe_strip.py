"""Exercise the production Gradle strip task on a fixture outside build outputs.

Uses the normal project wrapper and Android toolchain. It neither downloads a
replacement dependency nor substitutes the fixture into the app classpath.
"""
from pathlib import Path
import os
import subprocess
import tempfile
import zipfile

root = Path(__file__).resolve().parents[1]
entries = {
    "META-INF/MANIFEST.MF": b"Manifest-Version: 1.0\n\n",
    "org/schabi/newpipe/extractor/utils/Utils.class": b"remove-main",
    "org/schabi/newpipe/extractor/utils/Utils$Nested.class": b"remove-nested",
    "org/schabi/newpipe/extractor/utils/Utils2.class": b"keep-similar",
    "org/schabi/newpipe/extractor/Stream.class": bytes(range(256)),
    "metadata/config.json": b'{"keep":true}',
}
with tempfile.TemporaryDirectory(prefix="veyra-strip-") as directory:
    fixture = Path(directory)
    with zipfile.ZipFile(fixture / "input.jar", "w") as archive:
        for name, value in entries.items():
            archive.writestr(name, value)
    init = fixture / "verify.gradle"
    init.write_text("""
gradle.projectsEvaluated {
    def appProject = gradle.rootProject.project(':app')
    def production = appProject.tasks.named('stripNewPipeExtractorUtils').get()
    def fixture = new File(System.getProperty('veyra.strip.fixture.dir'))
    appProject.tasks.register('verifyStripFixture', production.class.superclass) {
        extractor.from(appProject.files(new File(fixture, 'input.jar')))
        outputJar.fileValue(new File(fixture, 'output.jar'))
    }
}
""")
    wrapper = [str(root / "gradlew.bat")] if os.name == "nt" else ["bash", str(root / "gradlew")]
    command = wrapper + [":app:verifyStripFixture", "--no-daemon", "--max-workers=2",
                         "-I", str(init), f"-Dveyra.strip.fixture.dir={fixture}"]
    subprocess.run(command, cwd=root, check=True)
    with zipfile.ZipFile(fixture / "output.jar") as output:
        assert output.testzip() is None
        expected = {name: value for name, value in entries.items()
                    if name not in {"org/schabi/newpipe/extractor/utils/Utils.class",
                                    "org/schabi/newpipe/extractor/utils/Utils$Nested.class"}}
        assert set(output.namelist()) == set(expected), output.namelist()
        for name, value in expected.items():
            assert output.read(name) == value, name
    print("Production strip-task fixture passed: duplicate/nested Utils removed; all other bytes preserved.")
