package com.daily.app.util

import android.content.Context
import android.provider.Settings
import android.os.Build
import java.security.MessageDigest

/**
 * 设备指纹工具类.
 *
 * 生成稳定的设备唯一标识，用于：
 * - 用户绑定设备（JWT 校验）
 * - 签到时上报 deviceId
 * - 异常登录检测
 *
 * 生成策略: Android ID + Build.FINGERPRINT + Build.BRAND + Build.MODEL，
 * 然后进行 SHA-256 哈希，确保：
 * - 跨应用唯一性高
 * - 不暴露用户隐私信息
 * - 设备重置后会变化（符合隐私要求）
 */
object DeviceFingerprintUtil {

    /**
     * 生成设备唯一标识.
     *
     * 使用 Android ID 作为主要来源，辅以系统构建信息，
     * 通过 SHA-256 哈希生成固定长度的十六进制字符串。
     *
     * @param context 应用上下文
     * @return 设备唯一标识（SHA-256 十六进制字符串）
     */
    fun generateDeviceId(context: Context): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: ""

        val brand = Build.BRAND ?: ""
        val model = Build.MODEL ?: ""
        val fingerprint = Build.FINGERPRINT ?: ""

        val raw = "$androidId|$brand|$model|$fingerprint"

        return sha256(raw)
    }

    /**
     * 对输入字符串进行 SHA-256 哈希.
     *
     * @param input 待哈希的字符串
     * @return 32 字节的十六进制表示（64 字符）
     */
    fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
