# Player source-preservation audit

```sh
JAVA_HOME=/path/to/jdk17 python verification/player-audit/run.py
```

Bootstrap the root Gradle wrapper first and use the same `GRADLE_USER_HOME`. The script uses the wrapper's cached Kotlin PSI parser and Git history; it needs the saved preview baseline `3bfcbcd89bca544e71fb4b17df5433d444737a82`.

The audit compares named event/value arguments at 27 existing control call sites and retains 109 original named-function parameter sequences, allowing new optional parameters and extracted controls. It covers transport, seek, volume, action capsules, menus and queue-row tap/remove/drag callbacks. It also byte-compares 194 shared/playback/authentication/Android-host source files with the preview baseline.

This catches removed/rewired calls in the covered source, but is not a runtime integration test or proof that every behavior is unchanged. Intended presentation changes and cancellation behavior are documented in [the player report](../PLAYER-REFINEMENT-RESULTS.md). The settings audit is separate and still compares against stable 0.1.3.
