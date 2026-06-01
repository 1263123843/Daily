package com.daily.app.di

import com.daily.app.data.repository.CheckinRepositoryImpl
import com.daily.app.data.repository.ContactRepositoryImpl
import com.daily.app.domain.repository.ICheckinRepository
import com.daily.app.domain.repository.IContactRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository 模块 - 绑定 Repository 接口到其实现类.
 *
 * 使用 @Binds 将抽象接口绑定到具体实现，
 * 使得依赖注入时可以通过接口类型获取实现实例。
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCheckinRepository(impl: CheckinRepositoryImpl): ICheckinRepository

    @Binds
    @Singleton
    abstract fun bindContactRepository(impl: ContactRepositoryImpl): IContactRepository
}
