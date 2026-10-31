package app.veyra.music

import app.veyra.music.data.*
import org.junit.Assert.*
import org.junit.Test

class UpdateAssetPolicyTest {
    private fun pick(vararg names: String, abis: List<String> = listOf("arm64-v8a", "armeabi-v7a")) =
        chooseUpdateApk(names.map { UpdateApkAsset(it, it) }, abis, "Veyra-")?.name

    @Test fun `universal wins even when a split is listed first`() {
        assertEquals("Veyra-0.2.0-universal-release.apk", pick("Veyra-0.2.0-arm64-v8a-release.apk", "Veyra-0.2.0-universal-release.apk"))
    }
    @Test fun `selects the supported ABI across both published naming conventions`() {
        assertEquals("Veyra-1_arm64-v8a.apk", pick("Veyra-1_x86.apk", "Veyra-1_arm64-v8a.apk"))
        assertEquals("Veyra-1-arm64-v8a-release.apk", pick("Veyra-1-armeabi-v7a-release.apk", "Veyra-1-arm64-v8a-release.apk"))
    }
    @Test fun `32 bit x86 never receives a 64 bit only split`() {
        assertNull(pick("Veyra-1-x86_64-release.apk", abis = listOf("x86")))
    }
    @Test fun `rejects other products debug builds and unsupported devices`() {
        assertNull(pick("VeyraTV-1.apk", "Veyra-1-debug.apk", "Veyra-1-dev-release.apk", "Veyra-1_x86.apk"))
    }
    @Test fun `plain universal legacy name remains supported`() {
        assertEquals("Veyra-v1.apk", pick("Veyra-v1.apk"))
    }
    @Test fun `stable publication upgrades its beta but never downgrades a newer beta`() {
        assertTrue(AppUpdateChecker.isNewer("0.2.1", "0.2.1-beta1"))
        assertFalse(AppUpdateChecker.isNewer("0.2.0", "0.2.1-beta1"))
        assertTrue(AppUpdateChecker.isNewer("0.10.0", "0.9.9"))
        assertFalse(AppUpdateChecker.isNewer("0.2.1", "0.2.1"))
    }
}
