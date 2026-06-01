package com.daily.app.domain.repository

import com.daily.app.domain.model.EmergencyContact
import kotlinx.coroutines.flow.Flow

/**
 * 联系人仓库接口.
 *
 * 定义了紧急联系人相关的核心数据操作契约。
 * 实现类负责处理本地缓存和远程同步。
 */
interface IContactRepository {

    /**
     * 获取用户所有紧急联系人（Flow 响应式）.
     *
     * @param userId 用户 ID
     * @return 联系人列表的 Flow
     */
    fun getAllByUserId(userId: String): Flow<List<EmergencyContact>>

    /**
     * 获取用户所有紧急联系人（一次性查询）.
     *
     * @param userId 用户 ID
     * @return 联系人列表
     */
    suspend fun getAllByUserIdSync(userId: String): List<EmergencyContact>

    /**
     * 添加紧急联系人.
     *
     * @param contact 联系人领域模型
     * @return 添加结果，成功时返回包含服务器返回 ID 的联系人对象
     */
    suspend fun add(contact: EmergencyContact): Result<EmergencyContact>

    /**
     * 删除紧急联系人.
     *
     * @param id 联系人 ID
     * @param userId 用户 ID
     * @return 删除结果
     */
    suspend fun delete(id: Long, userId: String): Result<Unit>
}
