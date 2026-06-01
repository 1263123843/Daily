package com.daily.app.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.daily.app.data.db.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

/**
 * 联系人数据访问对象.
 *
 * 提供紧急联系人的增删查操作，支持 Flow 响应式查询。
 */
@Dao
interface ContactDao {

    /**
     * 插入一条联系人记录.
     */
    @Insert
    suspend fun insert(contact: ContactEntity): Long

    /**
     * 批量插入联系人记录.
     */
    @Insert
    suspend fun insertAll(contacts: List<ContactEntity>)

    /**
     * 查询指定用户的所有联系人，Flow 响应式.
     */
    @Query("SELECT * FROM contacts WHERE user_id = :userId ORDER BY created_at ASC")
    fun getAllByUserId(userId: String): Flow<List<ContactEntity>>

    /**
     * 查询指定用户的所有联系人（一次性查询）.
     */
    @Query("SELECT * FROM contacts WHERE user_id = :userId ORDER BY created_at ASC")
    suspend fun getAllByUserIdSync(userId: String): List<ContactEntity>

    /**
     * 根据 ID 和用户 ID 查询单个联系人.
     */
    @Query("SELECT * FROM contacts WHERE id = :id AND user_id = :userId LIMIT 1")
    suspend fun getByIdAndUserId(id: Long, userId: String): ContactEntity?

    /**
     * 删除指定联系人.
     */
    @Delete
    suspend fun delete(contact: ContactEntity)

    /**
     * 根据 ID 删除联系人.
     */
    @Query("DELETE FROM contacts WHERE id = :id AND user_id = :userId")
    suspend fun deleteById(id: Long, userId: String): Int

    /**
     * 统计用户的联系人数量.
     */
    @Query("SELECT COUNT(*) FROM contacts WHERE user_id = :userId")
    suspend fun countByUserId(userId: String): Int

    /**
     * 清空用户所有联系人.
     */
    @Query("DELETE FROM contacts WHERE user_id = :userId")
    suspend fun deleteByUserId(userId: String)
}
