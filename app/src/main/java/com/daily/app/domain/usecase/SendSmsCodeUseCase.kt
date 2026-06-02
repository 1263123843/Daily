package com.daily.app.domain.usecase

import com.daily.app.data.remote.CheckinRemoteDataSource
import com.daily.app.data.remote.api.model.SmsSendRequest
import com.daily.app.util.PhoneUtil
import com.daily.app.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 发送短信验证码用例.
 *
 * 业务流程:
 * 1. 校验手机号格式
 * 2. 调用远程 API 发送验证码
 * 3. 返回发送结果
 *
 * @property remoteDataSource 远程数据源
 */
@Singleton
class SendSmsCodeUseCase @Inject constructor(
    private val remoteDataSource: CheckinRemoteDataSource
) : ISendSmsCodeUseCase {

    override suspend operator fun invoke(params: SendSmsCodeParams): Result<Unit> {
        return withContext(Dispatchers.IO) {
            val formattedPhone = PhoneUtil.formatPhone(params.phone)
            if (!PhoneUtil.isValidPhone(formattedPhone)) {
                return@withContext Result.Error(null, "请输入有效的手机号")
            }

            val request = SmsSendRequest(phone = formattedPhone)

            val result = remoteDataSource.sendSmsCode(request)
            if (result.isSuccess) {
                Result.Success(Unit)
            } else {
                val e = result.exceptionOrNull()
                Result.Error(e, "发送验证码失败: ${e?.message}")
            }
        }
    }
}
