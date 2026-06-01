package com.daily.app.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 签到记录实体.
 *
 * 存储用户的签到记录，包括位置信息和签到来源（自动/手动）.
 *
 * @property id 主键，自增
 * @property userId 用户ID
 * @property deviceId 设备ID
 * @property checkinTime 签到时间戳（毫秒）
 * @property latitude 纬度（可选）
 * @property longitude 经度（可选）
 * @property source 签到来源：auto 或 manual
 */
@Entity(tableName = "checkins")
data class CheckinEntity(
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
    val source: String
)
