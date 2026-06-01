package com.daily.app.di

import com.daily.app.domain.usecase.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * UseCase 模块 - 绑定所有 UseCase 接口到其实现类.
 *
 * 每个 UseCase 封装一个单一业务逻辑操作，遵循 Clean Architecture 的用例层设计原则。
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun bindCheckinUseCase(impl: CheckinUseCase): ICheckinUseCase

    @Binds
    @Singleton
    abstract fun bindSyncCheckinsUseCase(impl: SyncCheckinsUseCase): ISyncCheckinsUseCase

    @Binds
    @Singleton
    abstract fun bindGetUserStatusUseCase(impl: GetUserStatusUseCase): IGetUserStatusUseCase

    @Binds
    @Singleton
    abstract fun bindGetCheckinHistoryUseCase(impl: GetCheckinHistoryUseCase): IGetCheckinHistoryUseCase

    @Binds
    @Singleton
    abstract fun bindAddContactUseCase(impl: AddContactUseCase): IAddContactUseCase

    @Binds
    @Singleton
    abstract fun bindDeleteContactUseCase(impl: DeleteContactUseCase): IDeleteContactUseCase

    @Binds
    @Singleton
    abstract fun bindGetContactsUseCase(impl: GetContactsUseCase): IGetContactsUseCase

    @Binds
    @Singleton
    abstract fun bindSendSmsCodeUseCase(impl: SendSmsCodeUseCase): ISendSmsCodeUseCase

    @Binds
    @Singleton
    abstract fun bindVerifySmsCodeUseCase(impl: VerifySmsCodeUseCase): IVerifySmsCodeUseCase
}
