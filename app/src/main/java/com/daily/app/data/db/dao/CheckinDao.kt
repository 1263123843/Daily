package com.daily.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.daily.app.data.db.entity.CheckinEntity
import kotlinx.coroutines.flow.Flow

/**
 * 签到记录数据访问对象.
 *
 * 提供签到记录的增删查操作，支持 Flow 响应式查询。
 */
@Dao
interface CheckinDao {

    /**
     * 插入一条签到记录.
     */
    @Insert
    suspend fun insert(checkin: CheckinEntity): Long

    /**
     * 批量插入签到记录.
     */
    @Insert
    suspend fun insertAll(checkins: List<CheckinEntity>)

    /**
     * 查询指定用户的所有签到记录，按时间倒序排列（Flow 响应式）.
     */
    @Query("SELECT * FROM checkins WHERE user_id = :userId ORDER BY checkin_time DESC")
    fun getByUserId(userId: String): Flow<List<CheckinEntity>>

    /**
     * 查询指定用户的签到记录，非 Flow 版本（一次性查询）.
     */
    @Query("SELECT * FROM checkins WHERE user_id = :userId ORDER BY checkin_time DESC LIMIT :limit")
    suspend fun getByUserIdSync(userId: String, limit: Int = 50): List<CheckinEntity>

    /**
     * 获取最近 N 条签到记录（用于 UI 展示）.
     */
    @Query("SELECT * FROM checkins WHERE user_id = :userId ORDER BY checkin_time DESC LIMIT :limit")
    suspend fun getRecent(userId: String, limit: Int = 30): List<CheckinEntity>

    /**
     * 统计用户签到总数.
     */
    @Query("SELECT COUNT(*) FROM checkins WHERE user_id = :userId")
    suspend fun countByUserId(userId: String): Int

    /**
     * 删除指定 ID 的签到记录.
     */
    @Query("DELETE FROM checkins WHERE id = :id")
    suspend fun deleteById(id: Long)

    /**
     * 清空用户所有签到记录.
     */
    @Query("DELETE FROM checkins WHERE user_id = :userId")
    suspend fun deleteByUserId(userId: String)
}
