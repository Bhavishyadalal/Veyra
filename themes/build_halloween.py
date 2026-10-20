"""Compatibility entry point; rebuild both current theme packs and launcher assets."""
import runpy
from pathlib import Path
runpy.run_path(str(Path(__file__).with_name('build_themes.py')),run_name='__main__')
