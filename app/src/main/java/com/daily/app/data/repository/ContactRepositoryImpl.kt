package com.daily.app.data.repository

import com.daily.app.data.db.entity.ContactEntity
import com.daily.app.data.local.ContactLocalDataSource
import com.daily.app.data.remote.CheckinRemoteDataSource
import com.daily.app.data.remote.api.model.ContactRequest
import com.daily.app.domain.model.EmergencyContact
import com.daily.app.domain.repository.IContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 联系人仓库实现类.
 *
 * 采用「本地缓存 + 远程同步」策略：
 * - getAllByUserId(): 优先返回本地缓存，异步触发远程同步
 * - add(): 调用 API 创建 → 成功后写本地缓存
 * - delete(): 调用 API 删除 → 成功后删本地
 *
 * @property localDataSource 本地联系人数据源
 * @property remoteDataSource 远程数据源（包含联系人 API 调用）
 */
@Singleton
class ContactRepositoryImpl @Inject constructor(
    private val localDataSource: ContactLocalDataSource,
    private val remoteDataSource: CheckinRemoteDataSource
) : IContactRepository {

    /**
     * 获取用户所有紧急联系人（Flow 响应式）.
     *
     * 返回本地缓存的联系人列表。
     * 首次调用时如果本地为空，可触发一次远程拉取（此处简化实现）.
     */
    override fun getAllByUserId(userId: String): Flow<List<EmergencyContact>> {
        return localDataSource.getContactsByUserId(userId).map { entities ->
            entities.map { entityToDomain(it) }
        }
    }

    /**
     * 获取用户所有联系人（一次性查询）.
     */
    override suspend fun getAllByUserIdSync(userId: String): List<EmergencyContact> {
        return withContext(Dispatchers.IO) {
            val entities = localDataSource.getContactsByUserIdSync(userId)
            entities.map { entityToDomain(it) }
        }
    }

    /**
     * 添加紧急联系人.
     *
     * 先调 API 创建 → 成功后写本地缓存，确保一致性。
     */
    override suspend fun add(contact: EmergencyContact): Result<EmergencyContact> {
        return withContext(Dispatchers.IO) {
            try {
                val request = ContactRequest(
                    name = contact.name,
                    relationship = contact.relationship.name.lowercase(),
                    phoneEncrypted = contact.phone, // 加密由上层处理
                    phoneHash = "" // 哈希由上层计算后传入
                )

                when (val result = remoteDataSource.createContact(request)) {
                    is Result.Success -> {
                        // API 创建成功，写入本地缓存
                        val entity = ContactEntity(
                            userId = contact.userId,
                            name = contact.name,
                            relationship = contact.relationship.name.lowercase(),
                            phoneEncrypted = request.phoneEncrypted,
                            phoneHash = request.phoneHash,
                            isVerified = result.value.isVerified
                        )
                        localDataSource.saveContact(entity)
                        Result.success(contact.copy(contactId = result.value.contactId))
                    }
                    is Result.Failure -> Result.failure(result.exceptionOrNull() ?: RuntimeException("添加联系人失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 删除紧急联系人.
     *
     * 先调 API 删除 → 成功后删本地记录。
     */
    override suspend fun delete(id: Long, userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                when (val result = remoteDataSource.deleteContact(id)) {
                    is Result.Success -> {
                        // API 删除成功，删除本地缓存
                        localDataSource.deleteContactById(id, userId)
                        Result.success(Unit)
                    }
                    is Result.Failure -> Result.failure(result.exceptionOrNull() ?: RuntimeException("删除联系人失败"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 将 Entity 转换为 Domain Model.
     */
    private fun entityToDomain(entity: ContactEntity): EmergencyContact {
        return EmergencyContact(
            contactId = entity.id,
            userId = entity.userId,
            name = entity.name,
            relationship = when (entity.relationship.lowercase()) {
                "parent" -> Relationship.PARENT
                "partner" -> Relationship.PARTNER
                "friend" -> Relationship.FRIEND
                "roommate" -> Relationship.ROOMMATE
                else -> Relationship.OTHER
            },
            phone = "", // 本地不存储明文手机号
            isVerified = entity.isVerified
        )
    }
}
