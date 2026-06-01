package com.daily.app.data.local

import com.daily.app.data.db.dao.CheckinDao
import com.daily.app.data.db.dao.PendingCheckinDao
import com.daily.app.data.db.entity.CheckinEntity
import com.daily.app.data.db.entity.PendingCheckinEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 签到本地数据源.
 *
 * 封装 Room DAO 操作为响应式数据源，
 * 提供 Flow 形式的数据流供上层使用。
 *
 * @property checkinDao 签到记录 DAO
 * @property pendingCheckinDao 待上传签到 DAO
 */
@Singleton
class CheckinLocalDataSource @Inject constructor(
    private val checkinDao: CheckinDao,
    private val pendingCheckinDao: PendingCheckinDao
) {

    /**
     * 保存签到记录到本地数据库.
     */
    suspend fun saveCheckin(checkin: CheckinEntity): Long {
        return checkinDao.insert(checkin)
    }

    /**
     * 保存待上传签到到队列.
     */
    suspend fun savePendingCheckin(pending: PendingCheckinEntity): Long {
        return pendingCheckinDao.insert(pending)
    }

    /**
     * 获取用户签到历史（Flow 响应式）.
     */
    fun getCheckinsByUserId(userId: String): Flow<List<CheckinEntity>> {
        return checkinDao.getByUserId(userId)
    }

    /**
     * 获取用户最近 N 条签到记录.
     */
    suspend fun getRecentCheckins(userId: String, limit: Int = 30): List<CheckinEntity> {
        return checkinDao.getRecent(userId, limit)
    }

    /**
     * 统计用户签到总数.
     */
    suspend fun countCheckins(userId: String): Int {
        return checkinDao.countByUserId(userId)
    }

    // --- Pending Checkin 队列操作 ---

    /**
     * 获取所有未同步的签到记录（Flow）.
     */
    fun getUnsyncedPending(): Flow<List<PendingCheckinEntity>> {
        return pendingCheckinDao.getUnsynced()
    }

    /**
     * 获取未同步的签到记录（一次性查询，用于同步逻辑）.
     */
    suspend fun getUnsyncedPendingSync(): List<PendingCheckinEntity> {
        return pendingCheckinDao.getUnsyncedSync()
    }

    /**
     * 标记指定记录为已同步.
     */
    suspend fun markPendingAsSynced(id: Long) {
        pendingCheckinDao.markAsSynced(id)
    }

    /**
     * 批量标记已同步.
     */
    suspend fun markAllPendingAsSynced(ids: List<Long>) {
        if (ids.isNotEmpty()) {
            pendingCheckinDao.markAllAsSynced(ids)
        }
    }

    /**
     * 清理已同步的队列记录.
     */
    suspend fun cleanupSyncedPending() {
        pendingCheckinDao.deleteSynced()
    }

    /**
     * 统计未同步记录数量.
     */
    suspend fun countUnsyncedPending(): Int {
        return pendingCheckinDao.countUnsynced()
    }
}
