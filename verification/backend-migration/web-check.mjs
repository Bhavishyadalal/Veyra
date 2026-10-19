// Exercises real compiled web invite and endpoint helpers; not a full web-player build.
import { mkdtempSync, rmSync } from 'node:fs';
import { tmpdir } from 'node:os';
import { resolve, join, dirname } from 'node:path';
import { createRequire } from 'node:module';
import { fileURLToPath } from 'node:url';
import { spawnSync } from 'node:child_process';
import assert from 'node:assert/strict';

const root = resolve(dirname(fileURLToPath(import.meta.url)), '../..');
const out = mkdtempSync(join(tmpdir(), 'veyra-web-invites-'));
try {
  const build = spawnSync(process.env.TSC_BIN || 'tsc', [
    join(root, 'web/app/src/party/backend.ts'), join(root, 'web/app/src/party/invite.ts'),
    '--target', 'ES2022', '--module', 'commonjs', '--strict', '--skipLibCheck', '--outDir', out,
  ], { stdio: 'inherit' });
  if (build.status !== 0) process.exit(build.status || 1);
  const require = createRequire(import.meta.url);
  const { BACKEND_ORIGIN, isRetiredServer } = require(join(out, 'backend.js'));
  const { parseInvite, inviteUrl, sanitizeServerUrl } = require(join(out, 'invite.js'));
  assert.deepEqual(parseInvite(`${BACKEND_ORIGIN}/invite/ab12cd`), { code: 'AB12CD', serverUrl: BACKEND_ORIGIN });
  assert.equal(inviteUrl('ab12cd', null), `${BACKEND_ORIGIN}/invite/AB12CD`);
  assert.deepEqual(parseInvite('veyra://party/AB12CD?server=rooms.example.com'), { code: 'AB12CD', serverUrl: 'https://rooms.example.com' });
  assert.equal(inviteUrl('AB12CD', 'https://rooms.example.com/'), 'https://rooms.example.com/invite/AB12CD');
  assert.equal(parseInvite('https://bitchord.kushagrasingh.in/invite/AB12CD'), null);
  assert.equal(parseInvite('veyra://party/AB12CD?server=https%3A%2F%2Fapi.bitchord.kushagrasingh.in'), null);
  assert.equal(parseInvite('veyra://party/AB12CD?server='), null);
  assert.equal(parseInvite('veyra://party/AB12CD?server=https%3A%2F%2F'), null);
  assert.equal(sanitizeServerUrl('ftp://rooms.example.com'), null);
  assert.equal(sanitizeServerUrl('https://name:secret@rooms.example.com'), null);
  assert.equal(isRetiredServer('HTTP://API.BITCHORD.KUSHAGRASINGH.IN:80/api'), true);
  assert.equal(isRetiredServer('https://api.bitchord.kushagrasingh.in.example.com'), false);
  console.log('12 web endpoint/invite checks passed. Full web-player runtime not exercised.');
} finally { rmSync(out, { recursive: true, force: true }); }
