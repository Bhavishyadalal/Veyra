#!/usr/bin/env python3
"""Route generated catalogs to this build's track without changing any pack bytes."""
from pathlib import Path
import json
import re


def current_branch(root):
    source = (root / 'build.gradle.kts').read_text()
    version = re.search(r'extra\["veyraVersionName"\] = "([0-9.]+)"', source).group(1)
    suffix = re.search(r'extra\["veyraBetaSuffix"\] = "([a-z0-9]*)"', source).group(1)
    return f'preview/{version}-{suffix}' if suffix.startswith('beta') else 'main'


def main():
    root = Path(__file__).resolve().parents[1]
    branch = current_branch(root)
    for base, key, bundled in [('themes', 'themes', 'theme-catalog.json'),
                               ('asset-packs', 'packs', 'asset-catalog.json')]:
        path = root / base / 'catalog.json'
        catalog = json.loads(path.read_text())
        prefix = f'https://raw.githubusercontent.com/Bhavishyadalal/Veyra/refs/heads/{branch}/{base}/'
        for entry in catalog[key]:
            entry['url'] = prefix + f'packs/{entry["id"]}-v{entry["version"]}.zip'
            entry['preview'] = prefix + entry['id'] + ('/artwork/preview.webp' if base == 'themes' else '/preview.webp')
        data = json.dumps(catalog, indent=2) + '\n'
        path.write_text(data)
        (root / 'app/src/main/assets' / bundled).write_text(data)
        print(f'{base}: {len(catalog[key])} entries routed to {branch}')


if __name__ == '__main__':
    main()
