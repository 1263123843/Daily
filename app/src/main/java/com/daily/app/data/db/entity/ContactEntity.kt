package com.daily.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 联系人实体.
 *
 * 存储用户的紧急联系人信息，手机号以加密和哈希形式存储.
 *
 * @property id 主键，自增
 * @property userId 用户ID
 * @property name 联系人姓名
 * @property relationship 关系类型（parent/partner/friend/roommate/other）
 * @property phoneEncrypted AES 加密后的手机号
 * @property phoneHash 手机号 SHA256 哈希值（用于去重校验）
 * @property isVerified 是否已通过短信验证
 * @property createdAt 创建时间戳
 */
@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "relationship")
    val relationship: String,

    @ColumnInfo(name = "phone_encrypted")
    val phoneEncrypted: String,

    @ColumnInfo(name = "phone_hash")
    val phoneHash: String,

    @ColumnInfo(name = "is_verified")
    val isVerified: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
