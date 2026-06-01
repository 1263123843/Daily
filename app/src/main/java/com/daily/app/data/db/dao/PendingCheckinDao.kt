package com.daily.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.daily.app.data.db.entity.PendingCheckinEntity
import kotlinx.coroutines.flow.Flow

/**
 * 待上传签到队列数据访问对象.
 *
 * 管理离线优先策略中的待同步签到记录，
 * 支持查询未同步记录、标记已同步、清理已同步记录等操作。
 */
@Dao
interface PendingCheckinDao {

    /**
     * 插入一条待上传签到记录.
     */
    @Insert
    suspend fun insert(pending: PendingCheckinEntity): Long

    /**
     * 批量插入待上传签到记录.
     */
    @Insert
    suspend fun insertAll(pendings: List<PendingCheckinEntity>)

    /**
     * 查询所有未同步的签到记录（Flow 响应式）.
     *
     * 用于 UI 层展示当前离线队列状态。
     */
    @Query("SELECT * FROM pending_checkins WHERE synced = 0 ORDER BY created_at ASC")
    fun getUnsynced(): Flow<List<PendingCheckinEntity>>

    /**
     * 查询所有未同步的签到记录（一次性查询，用于同步逻辑）.
     *
     * @return 未按时间排序的待上传记录列表
     */
    @Query("SELECT * FROM pending_checkins WHERE synced = 0")
    suspend fun getUnsyncedSync(): List<PendingCheckinEntity>

    /**
     * 根据用户 ID 查询其未同步记录.
     */
    @Query("SELECT * FROM pending_checkins WHERE user_id = :userId AND synced = 0")
    suspend fun getUnsyncedByUserId(userId: String): List<PendingCheckinEntity>

    /**
     * 标记指定记录为已同步.
     *
     * @param id 记录 ID
     */
    @Query("UPDATE pending_checkins SET synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)

    /**
     * 批量标记为已同步.
     *
     * @param ids 需要标记的记录 ID 列表
     */
    @Query("UPDATE pending_checkins SET synced = 1 WHERE id IN (:ids)")
    suspend fun markAllAsSynced(ids: List<Long>)

    /**
     * 删除已同步的记录（用于清理）.
     *
     * @return 被删除的行数
     */
    @Query("DELETE FROM pending_checkins WHERE synced = 1")
    suspend fun deleteSynced(): Int

    /**
     * 统计未同步记录数量（用于 UI 显示角标或提示）.
     */
    @Query("SELECT COUNT(*) FROM pending_checkins WHERE synced = 0")
    suspend fun countUnsynced(): Int

    /**
     * 统计指定用户的未同步记录数量.
     */
    @Query("SELECT COUNT(*) FROM pending_checkins WHERE user_id = :userId AND synced = 0")
    suspend fun countUnsyncedByUserId(userId: String): Int

    /**
     * 清空所有数据（用于登出/重置场景）.
     */
    @Query("DELETE FROM pending_checkins")
    suspend fun deleteAll()
}
