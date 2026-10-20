# Veyra · 12-minute Render keep-alive

Stable **0.1.6** and completed preview **0.1.6-beta1** use the same Veyra backend. A single external cron job supports both app versions; installing a new APK is not required for the scheduler to help them. No repeating phone-side task is added.

| HTTP cron field | Value |
| --- | --- |
| Name | Veyra Render keep-alive |
| URL | `https://veyra-listen-together.onrender.com/healthz` |
| Method | `GET` |
| Schedule | Every 12 minutes / `*/12 * * * *` |
| Timezone | UTC, if asked |
| Timeout | 60 seconds or the provider's longest supported timeout |
| Retries | 2, if available |
| Authentication, headers and request body | None; leave blank |
| Expected result | HTTP 200; JSON `ok: true`, service `veyra-listen-together` |

Enable/save the job in your chosen external cron provider and use **Run now/Test**. Confirm a successful execution in its history. The endpoint already accepts these public GET requests and disables response caching; the request does not create a room or change playback.

The [backend cron guide](https://github.com/Bhavishyadalal/Veyra-backend/blob/main/CRON_SETUP.md) includes an external-machine crontab example and a reusable validated shell helper. It lives in the separate backend repository, not inside the Android app. A scheduler inside a sleeping Render process cannot wake that process.

The [backend GitHub Actions workflow](https://github.com/Bhavishyadalal/Veyra-backend/actions/workflows/render-keepalive.yml) remains a fallback at UTC minutes 07, 19, 31, 43 and 55. It uses the same helper as external shell cron. Scheduled GitHub workflows run from the backend repository's default main branch; adding one to this preview branch would not activate a separate scheduler.

You may keep both monitors. After verifying your external job, optionally disable only **Render keep-alive** in backend Actions if you do not want duplicate checks. Keep the backend test workflow enabled.

Cold starts can exceed short provider timeouts; enable retries and inspect execution history. Neither monitor guarantees uptime or prevents free-plan limits, restarts, deployments or in-memory room loss. GitHub schedules may run late or be disabled after inactivity. Use an always-on hosting plan when uninterrupted availability is required.

## Verification

The shared shell helper was run from a different working directory against a running local compiled Veyra server; HTTP and service/version validation passed. Its Bash syntax and unchanged GitHub 12-minute schedule were checked. The app's runtime sources, version numbers, package identity and signing configuration are unchanged by this cron update. Provider setup, deployed Render execution and always-on availability are not claimed as verified here.
