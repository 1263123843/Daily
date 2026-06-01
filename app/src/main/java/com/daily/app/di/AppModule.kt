package com.daily.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.daily.app.data.db.DailyDatabase
import com.daily.app.data.preferences.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

/**
 * 应用模块 - 提供本地数据库和 DataStore 实例.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDailyDatabase(@ApplicationContext context: Context): DailyDatabase {
        return Room.databaseBuilder(
            context,
            DailyDatabase::class.java,
            "daily_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCheckinDao(database: DailyDatabase) = database.checkinDao()

    @Provides
    @Singleton
    fun provideContactDao(database: DailyDatabase) = database.contactDao()

    @Provides
    @Singleton
    fun providePendingCheckinDao(database: DailyDatabase) = database.pendingCheckinDao()

    @Provides
    @Singleton
    fun provideUserDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.userDataStore
    }

    @Provides
    @Singleton
    fun provideUserPreferences(dataStore: DataStore<Preferences>): UserPreferences {
        return UserPreferences(dataStore)
    }
}
