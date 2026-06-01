package com.daily.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.daily.app.data.db.dao.CheckinDao
import com.daily.app.data.db.dao.ContactDao
import com.daily.app.data.db.dao.PendingCheckinDao
import com.daily.app.data.db.entity.CheckinEntity
import com.daily.app.data.db.entity.ContactEntity
import com.daily.app.data.db.entity.PendingCheckinEntity

/**
 * Daily 应用 Room 数据库.
 *
 * 管理三个数据表：签到记录、联系人、待上传签到队列.
 *
 * @property checkinDao 签到记录数据访问对象
 * @property contactDao 联系人数据访问对象
 * @property pendingCheckinDao 待上传签到数据访问对象
 */
@Database(
    entities = [
        CheckinEntity::class,
        ContactEntity::class,
        PendingCheckinEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DailyDatabase : RoomDatabase() {

    abstract fun checkinDao(): CheckinDao

    abstract fun contactDao(): ContactDao

    abstract fun pendingCheckinDao(): PendingCheckinDao
}
