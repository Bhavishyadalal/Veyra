# Open Veyra in Android Studio on Windows

This is the complete native Android source project. Extract the entire ZIP first. Open the **Veyra** folder containing **settings.gradle.kts**, **gradlew.bat** and the **app** folder. Do not open the ZIP itself or select only the app subfolder.

## 1. Install the project's build tools

Use Android Studio's SDK Manager to install these exact packages. Enable the package-details view when selecting a specific tool version.

| Component | Version |
| --- | --- |
| Gradle JDK | 17 |
| Android SDK platforms | 36 and 37.0 |
| Android SDK Build-Tools | 35.0.0 |
| Android NDK (side by side) | 27.0.12077973 |
| CMake | 3.22.1 |
| Android SDK Platform-Tools | Installed through SDK Manager |

The project's Gradle wrapper selects Gradle 8.11.1 and verifies its download checksum. The existing Android Gradle plugin 8.10.1 and Kotlin 2.3.20 versions are retained. No global Gradle installation is needed.

In Android Studio's Gradle settings, select a JDK 17 installation as the Gradle JDK. Install/select JDK 17 if the IDE's bundled runtime uses another version. Accept the SDK package licenses.

## 2. Open and sync

1. Choose **Open**, then select the extracted Veyra project root.
2. Set the Android SDK location when Android Studio asks. It normally creates local.properties automatically.
3. Let Gradle sync finish. The first sync downloads dependencies and can take several minutes.
4. Select **app** as the run module and **devDebug** as the build variant.
5. Connect your phone with USB debugging enabled, select it as the target device, then Run.

All Android modules are always declared in settings.gradle.kts. A missing SDK path now results in a useful SDK setup error instead of silently hiding the app, TV and watch modules.

If Android Studio cannot find the SDK automatically, copy local.properties.example to local.properties and replace the example SDK path with your own. Use forward slashes in a Windows path. Do not commit local.properties.

## 3. Build from a Windows terminal

Run these commands from the Veyra folder, with JAVA_HOME pointing to JDK 17:

```powershell
.\gradlew.bat :app:assembleDevDebug --max-workers=2
```

This builds **Veyra Dev**, which has its own application ID and can coexist with the normal Veyra app. Output: app/build/outputs/apk/dev/debug/.

To build the app labelled **Veyra**:

```powershell
.\gradlew.bat :app:assembleProdDebug --max-workers=2
```

Output: app/build/outputs/apk/prod/debug/. The arm64-v8a APK suits your Realme Narzo 60 5G; universal includes all supported CPU architectures. Debug APKs are development/test builds.

Run the phone unit tests with:

```powershell
.\gradlew.bat :app:testProdDebugUnitTest --max-workers=2
```

On macOS/Linux use ./gradlew instead of .\gradlew.bat. Use your phone as the target device to reduce RAM use on your PC.

The default phone build includes the watch APK. For a quicker phone-only development build, append -Pveyra.bundleWear=false; the watch-install feature then has no bundled watch APK in that build.

## 4. If sync or build fails

- **SDK location not found:** set the SDK location in Android Studio, or create local.properties from the example with your actual path.
- **Platform android-37.0 is missing:** install SDK Platform 37.0. A correctly installed platform has android.jar and source.properties directly inside its platform directory. Reinstall a damaged/incomplete SDK package through SDK Manager. An older installation may use the existing -PveyraCompileSdk=android-37 option.
- **NDK/CMake version missing:** install the exact versions in the table. The app and TV native builds now explicitly select NDK 27.0.12077973.
- **Dependency download failed / HTTP 403:** Gradle needs access to Google's Maven repository, Maven Central, the Gradle plugin portal and JitPack. Check your connection or configured proxy. A previous environment's proxy settings are not included in this ZIP.
- **Unsupported Java / Gradle runtime:** select JDK 17 for Gradle, then sync again.
- **No app run target after import:** check that you opened the project root and completed Gradle sync; select the app module and devDebug variant.

## 5. Signing and data

Android Studio generates a debug key on your computer for local testing. To update an existing APK signed elsewhere, use the same signing key. Android cannot update an app signed with a different key; back up its data before uninstalling it if replacement is necessary. The devDebug variant avoids replacing the normal Veyra package.

For a public release, create your own stable release keystore and use keystore.properties.example. Private keys, passwords and machine-specific SDK paths are not bundled in this source ZIP. Existing service configuration and app features are retained.

## Verification

See verification/ANDROID-STUDIO-BUILD.md for the build/test checks performed on this setup revision. Android Studio's graphical interface and physical-phone behavior are not claimed as tested here.
