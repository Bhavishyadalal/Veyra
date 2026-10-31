"""Exercise CI retry behavior without Gradle, network or signing credentials."""
from pathlib import Path
import os
import subprocess
import tempfile
import unittest

SCRIPT = Path(__file__).with_name("ci_gradle.sh").resolve()


class GradleRetryTest(unittest.TestCase):
    def invoke(self, message, first_code, second_code=0):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            fake = root / "gradlew"
            fake.write_text("#!/usr/bin/env python3\nfrom pathlib import Path\nimport sys\n"
                            "p=Path('calls'); n=len(p.read_text().splitlines()) if p.exists() else 0\n"
                            "with p.open('a') as f: f.write(' '.join(sys.argv[1:])+'\\n')\n"
                            f"print({message!r} if n == 0 else 'second attempt')\n"
                            f"sys.exit({first_code} if n == 0 else {second_code})\n")
            fake.chmod(0o755)
            result = subprocess.run(["bash", str(SCRIPT), ":app:assembleProdRelease"],
                                    cwd=root, env={**os.environ, "RUNNER_TEMP": folder},
                                    capture_output=True, text=True)
            calls = (root / "calls").read_text().splitlines()
            self.assertFalse(list(root.glob("veyra-gradle.*")), "temporary logs must be cleaned")
            return result.returncode, calls

    def test_success_runs_once(self):
        code, calls = self.invoke("BUILD SUCCESSFUL", 0)
        self.assertEqual((0, 1), (code, len(calls)))

    def test_compile_failure_is_not_retried_or_hidden(self):
        code, calls = self.invoke("Kotlin compile error", 17)
        self.assertEqual((17, 1), (code, len(calls)))

    def test_resolution_failure_refreshes_once(self):
        code, calls = self.invoke("Could not resolve all artifacts for configuration 'classpath'", 1)
        self.assertEqual((0, 2), (code, len(calls)))
        self.assertIn("--refresh-dependencies", calls[1])

    def test_second_failure_remains_a_failure(self):
        code, calls = self.invoke("Could not GET a dependency", 1, 19)
        self.assertEqual((19, 2), (code, len(calls)))


if __name__ == "__main__":
    unittest.main()
