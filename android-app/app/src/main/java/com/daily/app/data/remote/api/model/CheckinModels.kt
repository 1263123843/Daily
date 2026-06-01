package com.daily.app.data.remote.api.model

import com.google.gson.annotations.SerializedName

/**
 * 签到请求体.
 *
 * @property deviceId 设备唯一标识
 * @property latitude 纬度（可选）
 * @property longitude 经度（可选）
 * @property source 签到来源：auto 或 manual
 */
data class CheckinRequest(
    @SerializedName("device_id")
    val deviceId: String,

    @SerializedName("latitude")
    val latitude: Double? = null,

    @SerializedName("longitude")
    val longitude: Double? = null,

    @SerializedName("source")
    val source: String
)

/**
 * 签到响应体.
 *
 * @property checkinId 签到记录 ID
 * @property checkinTime 签到时间 (ISO 8601 格式)
 * @property status 签到状态描述（可选）
 */
data class CheckinResponse(
    @SerializedName("checkin_id")
    val checkinId: Long,

    @SerializedName("checkin_time")
    val checkinTime: String,

    @SerializedName("status")
    val status: String? = null
)

/**
 * 用户状态响应体.
 *
 * 包含用户的签到统计信息和安全状态.
 *
 * @property userId 用户 ID
 * @property nickname 用户昵称（可能为空）
 * @property status 用户安全状态：normal/abnormal/disabled
 * @property lastCheckinTime 最后一次签到时间（ISO 8601，可能为空）
 * @property lastCheckinAgoMinutes 距离上次签到的分钟数
 * @property consecutiveDays 连续签到天数
 * @property emergencyContactsCount 已添加的紧急联系人数量
 */
data class UserStatusResponse(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("nickname")
    val nickname: String? = null,

    @SerializedName("status")
    val status: String = "normal",

    @SerializedName("last_checkin_time")
    val lastCheckinTime: String? = null,

    @SerializedName("last_checkin_ago_minutes")
    val lastCheckinAgoMinutes: Long? = null,

    @SerializedName("consecutive_days")
    val consecutiveDays: Long? = 0L,

    @SerializedName("emergency_contacts_count")
    val emergencyContactsCount: Int? = 0
)
