package com.daily.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 待上传签到队列实体.
 *
 * 用于离线优先策略：签到先存入本地队列，成功同步到服务器后标记为已同步。
 * 网络恢复时自动重传未同步的记录。
 *
 * @property id 主键，自增
 * @property userId 用户ID
 * @property deviceId 设备ID
 * @property checkinTime 签到时间戳（毫秒）
 * @property latitude 纬度（可选）
 * @property longitude 经度（可选）
 * @property source 签到来源
 * @property retryCount 重试次数
 * @property createdAt 创建时间戳
 * @property synced 是否已同步到服务器
 */
@Entity(tableName = "pending_checkins")
data class PendingCheckinEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "device_id")
    val deviceId: String,

    @ColumnInfo(name = "checkin_time")
    val checkinTime: Long,

    @ColumnInfo(name = "latitude")
    val latitude: Double? = null,

    @ColumnInfo(name = "longitude")
    val longitude: Double? = null,

    @ColumnInfo(name = "source")
    val source: String,

    @ColumnInfo(name = "retry_count")
    val retryCount: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "synced")
    val synced: Boolean = false
)
