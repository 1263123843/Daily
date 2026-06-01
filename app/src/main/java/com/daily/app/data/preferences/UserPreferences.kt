package com.daily.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户偏好设置封装.
 *
 * 基于 AndroidX DataStore<Preferences> 实现的键值对存储，
 * 用于持久化用户配置和状态数据。
 *
 * 所有读写操作都是异步且安全的，DataStore 内部保证了事务一致性。
 *
 * @property dataStore Preferences DataStore 实例
 */
@Singleton
class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        // Preferences Keys
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val JWT_TOKEN = stringPreferencesKey("jwt_token")
        val CURRENT_WALLPAPER_URI = stringPreferencesKey("current_wallpaper_uri")
        val DEVICE_FINGERPRINT = stringPreferencesKey("device_fingerprint")
        val NICKNAME = stringPreferencesKey("nickname")
    }

    // ==================== Onboarding ====================

    /**
     * 是否已完成引导流程.
     */
    val isOnboardingCompleted: Flow<Boolean>
        get() = dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }

    /**
     * 设置引导完成状态.
     */
    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs ->
            prefs[ONBOARDING_COMPLETED] = completed
        }
    }

    // ==================== JWT Token ====================

    /**
     * JWT Token（Flow 响应式）.
     */
    val jwtToken: Flow<String?>
        get() = dataStore.data.map { it[JWT_TOKEN] }

    /**
     * 获取当前 JWT Token（一次性读取，用于拦截器等同步场景）.
     */
    suspend fun getJwtToken(): String? = jwtToken.first()

    /**
     * 保存 JWT Token.
     */
    suspend fun setJwtToken(token: String?) {
        dataStore.edit { prefs ->
            if (token != null) {
                prefs[JWT_TOKEN] = token
            } else {
                prefs.remove(JWT_TOKEN)
            }
        }
    }

    /**
     * 清除 JWT Token（登出时使用）.
     */
    suspend fun clearJwtToken() {
        dataStore.edit { prefs ->
            prefs.remove(JWT_TOKEN)
        }
    }

    // ==================== Wallpaper ====================

    /**
     * 当前壁纸 URI（Flow 响应式）.
     */
    val currentWallpaperUri: Flow<String?>
        get() = dataStore.data.map { it[CURRENT_WALLPAPER_URI] }

    /**
     * 保存当前壁纸 URI.
     */
    suspend fun setCurrentWallpaperUri(uri: String?) {
        dataStore.edit { prefs ->
            if (uri != null) {
                prefs[CURRENT_WALLPAPER_URI] = uri
            } else {
                prefs.remove(CURRENT_WALLPAPER_URI)
            }
        }
    }

    // ==================== Device Fingerprint ====================

    /**
     * 设备指纹（Flow 响应式）.
     */
    val deviceFingerprint: Flow<String?>
        get() = dataStore.data.map { it[DEVICE_FINGERPRINT] }

    /**
     * 获取设备指纹（一次性读取）.
     */
    suspend fun getDeviceFingerprint(): String? = deviceFingerprint.first()

    /**
     * 保存设备指纹.
     */
    suspend fun setDeviceFingerprint(fingerprint: String) {
        dataStore.edit { prefs ->
            prefs[DEVICE_FINGERPRINT] = fingerprint
        }
    }

    // ==================== Nickname ====================

    /**
     * 用户昵称（Flow 响应式）.
     */
    val nickname: Flow<String?>
        get() = dataStore.data.map { it[NICKNAME] }

    /**
     * 获取昵称（一次性读取）.
     */
    suspend fun getNickname(): String? = nickname.first()

    /**
     * 保存用户昵称.
     */
    suspend fun setNickname(name: String?) {
        dataStore.edit { prefs ->
            if (name != null) {
                prefs[NICKNAME] = name
            } else {
                prefs.remove(NICKNAME)
            }
        }
    }

    // ==================== Clear All (登出/重置) ====================

    /**
     * 清除所有用户偏好设置（用于登出或重置场景）.
     */
    suspend fun clearAll() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
