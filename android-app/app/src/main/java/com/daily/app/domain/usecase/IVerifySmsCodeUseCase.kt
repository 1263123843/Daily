package com.daily.app.domain.usecase

import com.daily.app.util.Result

/**
 * 验证短信验证码用例接口.
 */
interface IVerifySmsCodeUseCase {
    /**
     * 验证短信验证码.
     *
     * @param params 验证参数（手机号和验证码）
     * @return 验证结果，data 为是否验证通过
     */
    suspend operator fun invoke(params: VerifySmsCodeParams): Result<Boolean>
}

/**
 * 验证短信验证码参数.
 *
 * @property phone 手机号
 * @property code 验证码
 */
data class VerifySmsCodeParams(
    val phone: String,
    val code: String
)
