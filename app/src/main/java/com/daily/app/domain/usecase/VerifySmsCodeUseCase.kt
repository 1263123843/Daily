package com.daily.app.domain.usecase

import com.daily.app.data.remote.CheckinRemoteDataSource
import com.daily.app.data.remote.api.model.SmsVerifyRequest
import com.daily.app.util.PhoneUtil
import com.daily.app.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 验证短信验证码用例.
 *
 * 业务流程:
 * 1. 校验手机号格式
 * 2. 校验验证码非空
 * 3. 调用远程 API 验证短信验证码
 * 4. 返回验证结果（是否通过）
 *
 * @property remoteDataSource 远程数据源
 */
@Singleton
class VerifySmsCodeUseCase @Inject constructor(
    private val remoteDataSource: CheckinRemoteDataSource
) : IVerifySmsCodeUseCase {

    override suspend operator fun invoke(params: VerifySmsCodeParams): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            val formattedPhone = PhoneUtil.formatPhone(params.phone)
            if (!PhoneUtil.isValidPhone(formattedPhone)) {
                return@withContext Result.Error(null, "请输入有效的手机号")
            }

            if (params.code.isBlank()) {
                return@withContext Result.Error(null, "验证码不能为空")
            }

            val request = SmsVerifyRequest(phone = formattedPhone, code = params.code)

            val result = remoteDataSource.verifySmsCode(request)
            if (result.isSuccess) {
                val verified = result.getOrNull()?.verified ?: false
                Result.Success(verified)
            } else {
                val e = result.exceptionOrNull()
                Result.Error(e, "验证码校验失败: ${e?.message}")
            }
        }
    }
}
