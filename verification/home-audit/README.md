# Home source-preservation audit

```sh
JAVA_HOME=/path/to/jdk17 python verification/home-audit/run.py
```

Bootstrap the root Gradle wrapper first and use the same `GRADLE_USER_HOME`. The audit needs the Git baseline `0bac58fdbe77892bcf144deac97ac83d17cf3606` and the wrapper's cached Kotlin PSI compiler.

It compares 18 existing named/positional control-call contracts covering shelf/card actions, long presses, Google sign-in, refresh and retry; 41 original named-function parameter sequences are retained. The account prompt and compact row menu have explicit comparison aliases for their replacement native surfaces. Pager arrows are verified by the separate native component checks; their presentation is deliberately replaced.

All original function bodies outside the explicit Home refinement scope in HomeScreen/HomeCollections/Skeletons are compared as well. This catches accidental edits to the other shared shelf/card implementations. 223 player/playback/shared/auth/Android-host files byte-match the baseline. Existing carousel anchor conversion remains byte-identical.

This is source-preservation evidence, not a complete runtime integration test or proof of unchanged behavior. New optional enabled parameters, duplicate-title keys, empty-state handling, settled-page navigation and outgoing layout interaction guards are intentional changes. See [the results report](../HOME-REFINEMENT-RESULTS.md).
