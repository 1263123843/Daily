package com.daily.app.domain.usecase

import com.daily.app.util.Result

/**
 * 发送短信验证码用例接口.
 */
interface ISendSmsCodeUseCase {
    /**
     * 发送短信验证码到指定手机号.
     *
     * @param params 发送参数（手机号）
     * @return 发送结果
     */
    suspend operator fun invoke(params: SendSmsCodeParams): Result<Unit>
}

/**
 * 发送短信验证码参数.
 *
 * @property phone 目标手机号
 */
data class SendSmsCodeParams(
    val phone: String
)
