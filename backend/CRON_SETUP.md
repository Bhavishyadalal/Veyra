# External cron · Veyra Render keep-alive

Stable **0.1.5** and preview **0.1.5-beta1** use the same official backend. One external job supports both; no new APK or phone-side 12-minute timer is needed.

The existing public `GET /healthz` endpoint already supports ordinary HTTP cron services. It returns JSON with `ok: true`, service `veyra-listen-together`, protocol `veyra.v1` and the server version; responses are not cached. No account, API key, room code, membership token or request body is required. The check does not create a room or alter playback.

## HTTP cron service settings

| Setting | Value |
| --- | --- |
| Job name | Veyra Render keep-alive |
| URL | `https://veyra-listen-together.onrender.com/healthz` |
| HTTP method | `GET` |
| Interval | Every **12 minutes** |
| Cron expression, if available | `*/12 * * * *` |
| Timezone | UTC, if the provider asks |
| Timeout | 60 seconds or the longest available timeout |
| Retries, if available | 2 |
| Headers / authentication / body | Leave empty |
| Expected status | HTTP `200` |
| Expected content, if supported | `"ok":true` (allow optional spaces), plus service `veyra-listen-together` |

Save/enable the job, then choose the provider's **Run now** or **Test** action. Check its execution history for HTTP 200 and the Veyra JSON response. Exact UI labels vary by provider. Cold starts can exceed a short provider timeout: a timed-out first request may still wake the server; inspect the retry instead of treating it as proof the application is broken.

## Linux/macOS cron on a separate machine

Install `bash`, `curl` and Python 3; clone this repository on a machine that stays online. Test the helper from any working directory:

```sh
bash /absolute/path/Veyra-backend/scripts/keepalive.sh
```

On success it prints `Veyra service is healthy (version ...)`. Transport, non-2xx, malformed JSON, wrong-service or unhealthy responses exit nonzero. Requests have bounded retries/timeouts; temporary response files are removed.

Use `crontab -e` on that external machine and add this, replacing the path:

```cron
*/12 * * * * /bin/bash /absolute/path/Veyra-backend/scripts/keepalive.sh
```

Cron must have `curl` and `python3` on PATH. The helper resolves its validator relative to its own location, not cron's working directory. Standard cron notification/logging behavior applies; configure your host's logging if desired. Do not install this job inside the sleeping web service: a stopped process cannot wake itself. A Render Cron Job is a separate hosting product; this repository does not provision one or select a paid plan.

For local backend validation only, `VEYRA_HEALTH_URL` can override the helper's public health URL. It must point to a compatible Veyra health response.

## Existing GitHub Actions fallback

[Render keep-alive](https://github.com/Bhavishyadalal/Veyra-backend/actions/workflows/render-keepalive.yml) uses the same helper at UTC minutes 07, 19, 31, 43 and 55. Only the backend repository's default `main` branch schedules it; copying a scheduled workflow onto an Android preview branch would not activate it there.

You may keep this as a fallback alongside your external cron. If you prefer only your external scheduler after verifying it, disable **Render keep-alive** in GitHub Actions; do not disable the backend test workflow. Duplicate healthy requests are safe but not required.

Neither scheduler guarantees uptime or prevents Render restarts, deployments, free-tier quota exhaustion or loss of in-memory rooms. Public GitHub schedules can be delayed/dropped and disabled after inactivity. An always-on hosting plan is the reliable option when uninterrupted availability is required. The apps retain their cold-start/reconnect handling independently of this monitor.
