package com.daily.app.domain.usecase

import com.daily.app.domain.model.UserStatus
import com.daily.app.util.Result

/**
 * 获取用户状态用例接口.
 */
interface IGetUserStatusUseCase {
    /**
     * 获取当前用户状态信息.
     *
     * @return 用户状态结果
     */
    suspend operator fun invoke(): Result<UserStatus>
}
