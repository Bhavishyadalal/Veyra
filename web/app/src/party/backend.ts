/** Veyra-owned service; old operator URLs are recognized only to retire them. */
export const BACKEND_ORIGIN = 'https://veyra-listen-together.onrender.com';
export const EXPIRED_INVITE_MESSAGE = 'This invite uses the previous listening service. Ask the host to create a new Veyra room and share a new invite.';
const retiredHosts = new Set([
  'bitchord-listen-together.onrender.com',
  'api.bitchord.kushagrasingh.in',
  'bitchord.kushagrasingh.in',
]);
export function isRetiredServer(value: string | null | undefined): boolean {
  if (!value?.trim()) return false;
  try {
    const text = value.trim();
    const url = new URL(text.includes('://') ? text : `https://${text}`);
    return retiredHosts.has(url.hostname.toLowerCase().replace(/\.+$/, ''));
  } catch { return false; }
}
