/** The Vite dev server (web/app/vite.config.ts pins the port). */
const DEV_ORIGIN = 'http://localhost:5173';

/**
 * The web app origins this extension serves. Every message is checked against
 * this list, and it becomes externally_connectable (Chromium), the relay's
 * content-script matches (Firefox) and the DNR rules' initiatorDomains.
 *
 * No production web player is configured for Veyra yet. Development defaults to
 * localhost. Store builds must explicitly provide VEYRA_SITE_ORIGINS; the
 * marketing website is not a web player. The first origin is the popup link.
 */
export const SITE_ORIGINS: string[] = (
  process.env.VEYRA_SITE_ORIGINS ??
  (process.env.VEYRA_STORE_BUILD === '1' ? '' : DEV_ORIGIN)
)
  .split(',')
  .map((o) => o.trim().replace(/\/+$/, ''))
  .filter(Boolean);

if (SITE_ORIGINS.length === 0) {
  throw new Error('Set VEYRA_SITE_ORIGINS to your deployed Veyra web-player origin before packaging a store extension.');
}
for (const origin of SITE_ORIGINS) {
  const url = new URL(origin);
  const host = url.hostname.toLowerCase().replace(/\.$/, '');
  if (host === 'bitchord.in' || host.endsWith('.bitchord.in') ||
      host === 'bitchord-8bw.pages.dev' || host === 'bitchord.kushagrasingh.in' ||
      host.endsWith('.bitchord.kushagrasingh.in') || host === 'bitchord-listen-together.onrender.com') {
    throw new Error('Retired operator origins cannot be configured for the Veyra extension.');
  }
  const local = host === 'localhost' || host === '127.0.0.1' || host === '[::1]';
  if (url.origin !== origin || url.username || url.password ||
      (url.protocol !== 'https:' && !(url.protocol === 'http:' && local)) ||
      (process.env.VEYRA_STORE_BUILD === '1' && local)) {
    throw new Error('Use an exact HTTPS web-player origin; localhost HTTP is allowed only for development.');
  }
}

/**
 * Match patterns can't carry a port, so `http://localhost:5173` becomes
 * `http://localhost/*`. The exact origin, port included, is still enforced at
 * runtime against SITE_ORIGINS.
 */
export function matchPatternsFor(origins: string[]): string[] {
  return [...new Set(origins.map((o) => {
    const url = new URL(o);
    return `${url.protocol}//${url.hostname}/*`;
  }))];
}
