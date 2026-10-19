# Settings preservation audit

Run from the repository root on a Git checkout with the saved stable baseline available, using JDK 17 and the existing Gradle cache:

```sh
JAVA_HOME=/path/to/jdk17 GRADLE_USER_HOME=/path/to/gradle-cache python verification/settings-audit/run.py
```

The script uses Kotlin PSI from the pinned Gradle distribution to compare current source with stable commit `8a03bb769aeeea5b76ed36dc44db28aa950ca5e9`. It checks named UI action/value contracts for settings rows, subordinate toggles, sliders (including limits/steps), selectors, account entries, destructive actions and dialog actions. It also checks that original named functions still exist with their original parameter order, allowing optional parameters appended by the extracted reusable controls.

Byte comparisons additionally protect the complete authentication source directory, MainActivity routing, AppSettings preference persistence and AppIcon apply/rollback logic. The reviewed four independent integration-switch callbacks are retained separately from their row editor/account actions.

Current result: 160 existing named UI call contracts and 131 original named functions retained. This is a preservation audit, not proof that network authentication, playback, file pickers or Android system integration work. Reproducing the Git comparison requires a checkout with history; a downloaded source ZIP is enough for Android Studio and the native component renderer, but does not contain Git history.
