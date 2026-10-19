// Exercise the actual extension configuration without building/publishing an extension.
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const assert = require('node:assert/strict');
const ts = require(process.env.TYPESCRIPT_PATH || 'typescript');
const source = fs.readFileSync(path.resolve(__dirname, '../../web/extension/site.config.ts'), 'utf8');
const compiled = ts.transpileModule(source, { compilerOptions: { target: ts.ScriptTarget.ES2022, module: ts.ModuleKind.CommonJS } }).outputText;
function load(env) {
  const context = { exports: {}, process: { env }, URL };
  vm.runInNewContext(compiled, context);
  return context.exports;
}
let checks = 0;
function check(body) { body(); checks++; }
check(() => assert.equal(load({}).SITE_ORIGINS.join(','), 'http://localhost:5173'));
check(() => assert.equal(load({ VEYRA_SITE_ORIGINS: 'https://player.example.com' }).SITE_ORIGINS[0], 'https://player.example.com'));
check(() => assert.equal(load({ VEYRA_STORE_BUILD: '1', VEYRA_SITE_ORIGINS: 'https://player.example.com' }).SITE_ORIGINS[0], 'https://player.example.com'));
check(() => assert.throws(() => load({ VEYRA_STORE_BUILD: '1' })));
for (const origin of [
  'https://bitchord.in', 'https://bitchord-8bw.pages.dev',
  'https://bitchord.kushagrasingh.in', 'https://api.bitchord.kushagrasingh.in',
  'https://bitchord-listen-together.onrender.com', 'https://player.bitchord.in',
  'http://localhost:5173', 'http://127.0.0.1:5173',
  'https://user:secret@player.example.com', 'https://player.example.com/path',
  'http://player.example.com', 'https://player.example.com?x=y',
]) {
  check(() => assert.throws(() => load({ VEYRA_STORE_BUILD: '1', VEYRA_SITE_ORIGINS: origin })));
}
console.log(`${checks} extension origin checks passed; extension runtime/store publishing not tested.`);
