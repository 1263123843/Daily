package com.daily.app.data.remote.api.model

import com.google.gson.annotations.SerializedName

/**
 * 发送短信验证码请求体.
 *
 * @property phone 手机号码（明文）
 */
data class SmsSendRequest(
    @SerializedName("phone")
    val phone: String
)

/**
 * 验证短信验证码请求体.
 *
 * @property phone 手机号码
 * @property code 短信验证码
 */
data class SmsVerifyRequest(
    @SerializedName("phone")
    val phone: String,

    @SerializedName("code")
    val code: String
)

/**
 * 短信验证码验证响应体.
 *
 * @property verified 验证是否通过
 */
data class SmsVerifyResponse(
    @SerializedName("verified")
    val verified: Boolean
)
