package com.daily.app.domain.model

/**
 * 设备信息领域模型.
 *
 * 封装签到时需要的设备指纹信息，用于后端识别设备合法性。
 *
 * @property deviceId 设备唯一标识（SHA-256 哈希）
 * @property androidVersion 安卓系统版本（如 "13"）
 * @property appVersion 应用版本号（如 "1.0.0"）
 * @property modelName 设备型号（如 "Pixel 7"）
 */
data class DeviceInfo(
    val deviceId: String,
    val androidVersion: String,
    val appVersion: String,
    val modelName: String
)
