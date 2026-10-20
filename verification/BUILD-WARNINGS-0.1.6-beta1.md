# 0.1.6-beta1 build-log review

The supplied Windows log contains **115 Kotlin warnings** and three kinds of
tooling notices. It reaches `:app:minifyProdReleaseWithR8` without a recorded
failure. The log ends there; it does not establish that APK packaging finished.

## Source changes

- Replace nullable `JSONObject.optString` fallback arguments with empty strings,
  while preserving the existing absent/blank/literal-`null` filtering for saved
  playlist metadata.
- Remove redundant OkHttp 5 response-body null handling, casts, numeric
  conversions and conditions already proven by Kotlin's type analysis. HTTP
  status checks, parsing failures and stream closing remain in place.
- Use the suspending Compose `LocalClipboard`/`ClipEntry` API for room-code and
  track-log copy actions. Room codes, share actions and clipboard feedback remain.
- Use AutoMirrored Sort, Undo, Logout, Volume and Playlist icons for RTL support.
- Opt in to `FlowPreview` only for the SMB configuration debounce declaration.
- Replace deprecated Media3 folder metadata with explicit folder media types and
  browsability; retain all Android Auto/library folders and their commands.
- Use current Media3 audio-output speed configuration, connection-result builder,
  playback-resumption callback and audio-processor flush signatures. Preserve the
  spatial processor's direct Float32 reconfiguration reset as well.
- Obtain Bluetooth adapters from `BluetoothManager`; preserve permission/error
  handling and older/OEM codec telemetry fallbacks.
- Use the correct AndroidX annotation opt-in for Media3's Java `UnstableApi` marker.

The [site-by-site review](warnings016/reviewed-warnings.json) lists all original
warning locations. Original line numbers refer to the source reviewed in the
uploaded log, not the edited files.

## Intentional compatibility APIs

Four old Media3 device-volume callback overloads remain for older controllers and
are now explicitly marked deprecated; their flag-taking overloads remain current.
The established package-installer fallback, precision-audio buffer override copy,
and OEM Bluetooth payload/numeric codec fallbacks retain narrowly scoped
deprecation annotations with explanations. They are not replaced by no-ops.
No project-wide warning suppression or warnings-as-errors setting was added.

## Tooling notices that can still appear

| Notice | Meaning and handling |
| --- | --- |
| Experimental `android.experimental.lint.version=9.4.1` | This is the existing independent lint pin for newer Kotlin metadata in dependencies. Removing it merely to hide the notice can break lint. Keep it until a tested toolchain upgrade replaces it. |
| SDK XML v4 encountered by a v3 parser | The SDK and bundled Android tooling are from different generations. Keep Android Studio/SDK command-line tools consistent; a coordinated AGP/Gradle upgrade needs its own validation. This notice alone is not a compile failure, and no SDK files are edited here. |
| R8 resource provider does not support async parsing | The current AGP provider cannot offer R8 that parsing optimization. Keep the existing R8 pin required for dependency metadata and register-allocation fixes. This warning is not evidence of a failed shrink; R8/resource shrinking remain enabled. |

## Verification

- **85 focused app regression tests passed.**
- **34 audio/artwork tests passed:** 30 existing equalizer/embedded-art tests and
  four new tests proving that modern flush/reconfiguration clears previous-stream
  EQ, spatial and transition-filter history.
- **13 real production sources**, the API contract probe and three JUnit classes
  compiled with **Kotlin 2.3.20 and `-Werror`** against real Android API 37 and
  Media3 1.11.0 jars. The selected compile includes the playlist store and DSP
  changes. API contracts also check Compose clipboard, AndroidX opt-in,
  media-folder types, resumption, connection and audio-output builder signatures.
- The targeted harness uses cached coroutines 1.9.0 for its small flow/type probe;
  it does not resolve or verify the full application's transitive dependency graph.
- All 40 changed production Kotlin files pass Kotlin PSI syntax parsing. Syntax
  parsing is not a full Android/Compose type check.
- Official backend endpoint and backend snapshot manifest checks pass; this change
  does not modify the server, signing settings or package name.

Full `:app:compileProdReleaseKotlin` was attempted but stopped **before Kotlin
compilation** at `:app:stripNewPipeExtractorUtils`: this environment received
**HTTP 403 from JitPack** for `NewPipeExtractor:v0.26.3`. No full Android build,
R8 completion, APK or device behavior is claimed. Dependency verification and TLS
remain enabled; no substitute NewPipe binary or fake SDK package was introduced.

The app remains **0.1.6-beta1 / code 21** on its preview branch. Stable **0.1.5**
Android code is unchanged. The refreshed beta source ZIP includes this review,
reproducible checks and the same Veyra-owned backend snapshot.

## Repeat the focused checks

After bootstrapping the repository's Gradle/JDK/Android SDK environment:

```sh
python3 verification/run_focused_kotlin_tests.py
python3 verification/warnings016/run.py
python3 scripts/verify_app_backend.py
python3 scripts/verify_backend_mirror.py
```

Set `JAVA_HOME`, `GRADLE_USER_HOME` and `ANDROID_HOME` to your toolchain paths.
The second command downloads six pinned Google Maven AARs into a temporary
directory, verifies their extracted class-jar hashes, compiles selected sources,
and runs the audio/artwork checks. `--api-jars DIRECTORY` reuses those extracted
jars without downloading. No jar is committed to this repository. These focused
checks supplement, and do not replace, a complete Android release build.
