package com.daily.app.service

import android.content.Context
import android.os.Build
import dagger.hilt.EntryPoints
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.scopes.ApplicationScoped
import com.daily.app.data.preferences.UserPreferences
import com.daily.app.domain.model.DeviceInfo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/**
 * Singleton provider for device information.
 *
 * Gathers device metadata needed for check-in identification and analytics:
 * - Device fingerprint (from [UserPreferences], generated during onboarding)
 * - Android OS version ([Build.VERSION.RELEASE])
 * - App version ([android.content.pm.PackageManager])
 * - Device model name ([Build.MODEL])
 *
 * Scoped to the application lifecycle via `@ApplicationScoped` (equivalent to `@Singleton`
 * in Hilt's application component). All values are read lazily on each call to
 * [getDeviceInfo] to ensure freshness.
 */
@ApplicationScoped
class DeviceInfoProvider @JvmOverloads constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences,
) {

    /**
     * Returns a [DeviceInfo] object populated with the current device's metadata.
     *
     * This is a blocking call because it reads from DataStore (via [UserPreferences]).
     * Call from a background thread or use [getDeviceInfoAsync] for coroutine contexts.
     */
    fun getDeviceInfo(): DeviceInfo {
        val deviceFingerprint = runBlocking { userPreferences.getDeviceFingerprint() }
        val appVersion = getAppVersion()
        return DeviceInfo(
            deviceId = deviceFingerprint.ifEmpty { "unknown" },
            androidVersion = Build.VERSION.RELEASE,
            appVersion = appVersion.ifEmpty { "unknown" },
            modelName = Build.MODEL,
        )
    }

    /**
     * Suspending version of [getDeviceInfo] for use in coroutines.
     * Avoids the `runBlocking` overhead by collecting the flow directly.
     */
    suspend fun getDeviceInfoAsync(): DeviceInfo {
        val deviceFingerprint = userPreferences.getDeviceFingerprint()
        val appVersion = getAppVersion()
        return DeviceInfo(
            deviceId = deviceFingerprint.ifEmpty { "unknown" },
            androidVersion = Build.VERSION.RELEASE,
            appVersion = appVersion.ifEmpty { "unknown" },
            modelName = Build.MODEL,
        )
    }

    /**
     * Reads the app's version name from the package manager.
     * Falls back to "unknown" if the package manager is unavailable.
     */
    private fun getAppVersion(): String {
        return try {
            context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName
                .orEmpty()
        } catch (e: Exception) {
            "unknown"
        }
    }
}
