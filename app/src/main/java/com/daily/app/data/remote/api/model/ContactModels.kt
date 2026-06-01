package com.daily.app.data.remote.api.model

import com.google.gson.annotations.SerializedName

/**
 * 创建联系人请求体.
 *
 * @property name 联系人姓名
 * @property relationship 关系类型
 * @property phoneEncrypted AES 加密后的手机号
 * @property phoneHash 手机号 SHA256 哈希值
 */
data class ContactRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("relationship")
    val relationship: String,

    @SerializedName("phone_encrypted")
    val phoneEncrypted: String,

    @SerializedName("phone_hash")
    val phoneHash: String
)

/**
 * 联系人响应体.
 *
 * @property contactId 联系人 ID
 * @property name 联系人姓名
 * @property relationship 关系类型
 * @property phoneMasked 脱敏手机号（如 138****5678）
 * @property isVerified 是否已通过短信验证
 */
data class ContactResponse(
    @SerializedName("contact_id")
    val contactId: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("relationship")
    val relationship: String,

    @SerializedName("phone_masked")
    val phoneMasked: String? = null,

    @SerializedName("is_verified")
    val isVerified: Boolean = false
)
