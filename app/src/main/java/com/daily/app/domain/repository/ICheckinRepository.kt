package com.daily.app.domain.repository

import com.daily.app.domain.model.Checkin
import kotlinx.coroutines.flow.Flow

/**
 * 签到仓库接口.
 *
 * 定义了签到相关的核心数据操作契约。
 * 实现类负责处理本地缓存、远程同步和离线队列管理。
 */
interface ICheckinRepository {

    /**
     * 保存签到记录（离线优先）.
     *
     * 先写入本地数据库，再尝试同步到服务器。
     * 即使网络不可用，本地保存成功即视为成功。
     *
     * @param checkin 签到领域模型
     * @return 保存结果
     */
    suspend fun save(checkin: Checkin): Result<Checkin>

    /**
     * 获取用户最近的签到记录（一次性查询）.
     *
     * @param userId 用户 ID
     * @param limit 最大返回条数
     * @return 签到记录列表
     */
    suspend fun getRecentByUserId(userId: String, limit: Int): List<Checkin>

    /**
     * 观察用户签到记录（Flow 响应式）.
     *
     * @param userId 用户 ID
     * @return 签到记录列表的 Flow
     */
    fun observeRecentByUserId(userId: String): Flow<List<Checkin>>

    /**
     * 同步所有未上传的签到记录到服务器.
     *
     * @param userId 用户 ID
     * @return 同步成功的记录数
     */
    suspend fun syncPending(userId: String): Int
}
