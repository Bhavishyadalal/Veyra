/**
 * Party invites (JamInviteLink.kt): `https://veyra-listen-together.onrender.com/invite/<CODE>`,
 * optionally `?server=<custom server>`, and `veyra://party/<CODE>`. On the
 * web the first is a route of this app (`/invite/:code`).
 */

import { BACKEND_ORIGIN, isRetiredServer } from './backend';

export const INVITE_ORIGIN = BACKEND_ORIGIN;
export const CODE_LENGTH = 6;

/** Letters and digits only, upper case; null unless it's six of them. */
export function cleanCode(raw: string): string | null {
  const cleaned = raw.replace(/[^A-Za-z0-9]/g, '').toUpperCase();
  return cleaned.length === CODE_LENGTH ? cleaned : null;
}

export function sanitizeServerUrl(raw: string | null | undefined): string | null {
  const trimmed = raw?.trim();
  if (!trimmed) return null;
  const withScheme = trimmed.includes('://') ? trimmed : `https://${trimmed}`;
  try {
    const url = new URL(withScheme);
    return /^https?:$/.test(url.protocol) && url.host && !url.username && !url.password && !url.search && !url.hash ? withScheme.replace(/\/+$/, '') : null;
  } catch {
    return null;
  }
}

export function parseInvite(value: string): { code: string; serverUrl: string | null } | null {
  let url: URL;
  try {
    url = new URL(value);
  } catch {
    return null;
  }
  const rawServer = url.searchParams.get('server');
  const server = sanitizeServerUrl(rawServer);
  if (isRetiredServer(value) || isRetiredServer(rawServer) || (rawServer !== null && !server)) return null;
  if (url.protocol === 'veyra:' && url.host.toLowerCase() === 'party') {
    const code = cleanCode(url.pathname.replace(/^\/+|\/+$/g, '') || url.searchParams.get('code') || '');
    return code ? { code, serverUrl: server } : null;
  }
  const match = url.pathname.match(/^\/invite\/([A-Za-z0-9]{6})$/);
  if ((url.protocol === 'https:' || url.protocol === 'http:') && match) {
    return { code: match[1]!.toUpperCase(), serverUrl: server ?? url.origin };
  }
  return null;
}

/** The link to share (JamInviteLink.url): a custom server's own invite page, else the public site's. */
export function inviteUrl(code: string, customServer: string | null): string {
  const base = customServer?.trim().replace(/\/+$/, '');
  const origin = base && base.toLowerCase() !== INVITE_ORIGIN ? base : INVITE_ORIGIN;
  return `${origin}/invite/${code.toUpperCase()}`;
}
