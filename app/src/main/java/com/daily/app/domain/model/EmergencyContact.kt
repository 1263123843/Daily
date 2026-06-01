package com.daily.app.domain.model

/**
 * 紧急联系人领域模型.
 *
 * 表示用户添加的一个紧急联系人，包含姓名、关系、手机号和验证状态。
 *
 * @property contactId 联系人唯一标识
 * @property userId 所属用户 ID
 * @property name 联系人姓名
 * @property relationship 与用户的关系类型
 * @property phone 手机号（明文，仅内存中使用，不持久化）
 * @property isVerified 是否已通过短信验证
 */
data class EmergencyContact(
    val contactId: Long = 0L,
    val userId: String,
    val name: String,
    val relationship: Relationship,
    val phone: String,
    val isVerified: Boolean = false
)

/**
 * 联系人关系类型枚举.
 */
enum class Relationship {
    /** 父母/长辈 */
    PARENT,

    /** 伴侣/配偶 */
    PARTNER,

    /** 朋友 */
    FRIEND,

    /** 室友/同事 */
    ROOMMATE,

    /** 其他关系 */
    OTHER
}
