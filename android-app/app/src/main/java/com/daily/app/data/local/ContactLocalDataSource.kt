package com.daily.app.data.local

import com.daily.app.data.db.dao.ContactDao
import com.daily.app.data.db.entity.ContactEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 联系人本地数据源.
 *
 * 封装 ContactDao 操作，提供 Flow 响应式的联系人数据访问接口。
 *
 * @property contactDao 联系人 DAO
 */
@Singleton
class ContactLocalDataSource @Inject constructor(
    private val contactDao: ContactDao
) {

    /**
     * 保存联系人到本地数据库.
     */
    suspend fun saveContact(contact: ContactEntity): Long {
        return contactDao.insert(contact)
    }

    /**
     * 批量保存联系人（用于从远程同步后缓存）.
     */
    suspend fun saveAllContacts(contacts: List<ContactEntity>) {
        contactDao.insertAll(contacts)
    }

    /**
     * 获取用户所有联系人（Flow 响应式）.
     */
    fun getContactsByUserId(userId: String): Flow<List<ContactEntity>> {
        return contactDao.getAllByUserId(userId)
    }

    /**
     * 获取用户所有联系人（一次性查询）.
     */
    suspend fun getContactsByUserIdSync(userId: String): List<ContactEntity> {
        return contactDao.getAllByUserIdSync(userId)
    }

    /**
     * 根据 ID 和用户 ID 查询单个联系人.
     */
    suspend fun getContactById(id: Long, userId: String): ContactEntity? {
        return contactDao.getByIdAndUserId(id, userId)
    }

    /**
     * 删除指定联系人.
     */
    suspend fun deleteContact(contact: ContactEntity) {
        contactDao.delete(contact)
    }

    /**
     * 根据 ID 删除联系人.
     */
    suspend fun deleteContactById(id: Long, userId: String) {
        contactDao.deleteById(id, userId)
    }

    /**
     * 统计用户的联系人数量.
     */
    suspend fun countContacts(userId: String): Int {
        return contactDao.countByUserId(userId)
    }

    /**
     * 清空用户所有联系人（登出时使用）.
     */
    suspend fun clearContacts(userId: String) {
        contactDao.deleteByUserId(userId)
    }
}
