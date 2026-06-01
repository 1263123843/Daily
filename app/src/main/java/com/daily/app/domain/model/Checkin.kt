package com.daily.app.domain.model

import java.time.Instant

/**
 * 签到记录领域模型.
 *
 * 表示一次完整的签到操作，包含时间、位置和来源信息。
 *
 * @property checkinId 签到记录唯一标识
 * @property userId 执行签到的用户 ID
 * @property checkinTime 签到发生的时刻
 * @property latitude 签到时的纬度（可选）
 * @property longitude 签到时的经度（可选）
 * @property source 签到来源（自动解锁 / 手动点击）
 * @property deviceId 设备唯一标识
 */
data class Checkin(
    val checkinId: Long = 0L,
    val userId: String,
    val checkinTime: Instant,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val source: CheckinSource = CheckinSource.MANUAL,
    val deviceId: String? = null
)

/**
 * 签到来源枚举.
 */
enum class CheckinSource {
    /** 自动签到（用户解锁屏幕时触发） */
    AUTO_UNLOCK,

    /** 手动签到（用户在 App 内主动点击） */
    MANUAL
}
