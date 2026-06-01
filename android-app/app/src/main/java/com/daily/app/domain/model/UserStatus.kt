package com.daily.app.domain.model

/**
 * 用户状态领域模型.
 *
 * 表示当前用户的安全状态概览，包括签到统计和联系人信息。
 *
 * @property userId 用户 ID
 * @property nickname 用户昵称（可能为空）
 * @property status 用户安全状态：normal / abnormal / disabled
 * @property lastCheckinTime 最后一次签到时间（ISO 8601 格式，可能为空）
 * @property lastCheckinAgoMinutes 距离上次签到的分钟数
 * @property consecutiveDays 连续签到天数
 * @property emergencyContactsCount 已添加的紧急联系人数量
 */
data class UserStatus(
    val userId: String,
    val nickname: String? = null,
    val status: String = "normal",
    val lastCheckinTime: String? = null,
    val lastCheckinAgoMinutes: Long? = null,
    val consecutiveDays: Long? = 0L,
    val emergencyContactsCount: Int? = 0
)
