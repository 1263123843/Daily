package com.daily.app.domain.usecase

import com.daily.app.data.remote.CheckinRemoteDataSource
import com.daily.app.data.remote.api.model.SmsSendRequest
import com.daily.app.data.remote.api.model.SmsVerifyRequest
import com.daily.app.domain.model.EmergencyContact
import com.daily.app.domain.repository.IContactRepository
import com.daily.app.util.Constants
import com.daily.app.util.PhoneUtil
import com.daily.app.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 添加紧急联系人用例.
 *
 * 业务流程:
 * 1. 校验参数（姓名非空、手机号合法、联系人数量不超过上限）
 * 2. 发送短信验证码到目标手机号
 * 3. 验证用户输入的短信验证码
 * 4. 验证通过后，通过 Repository 保存到本地和远程
 *
 * @property repository 联系人仓库
 * @property remoteDataSource 远程数据源（提供 SMS 相关 API）
 */
@Singleton
class AddContactUseCase @Inject constructor(
    private val repository: IContactRepository,
    private val remoteDataSource: CheckinRemoteDataSource
) : IAddContactUseCase {

    override suspend operator fun invoke(params: AddContactParams): Result<EmergencyContact> {
        return withContext(Dispatchers.IO) {
            // 1. 参数校验
            if (params.name.isBlank()) {
                return@withContext Result.Error(null, "联系人姓名不能为空")
            }

            val formattedPhone = PhoneUtil.formatPhone(params.phone)
            if (!PhoneUtil.isValidPhone(formattedPhone)) {
                return@withContext Result.Error(null, "请输入有效的手机号")
            }

            // 2. 检查联系人数量上限
            val existingContacts = repository.getAllByUserIdSync(params.userId)
            if (existingContacts.size >= Constants.MAX_CONTACTS) {
                return@withContext Result.Error(null, "紧急联系人数量已达上限（${Constants.MAX_CONTACTS}人）")
            }

            // 3. 检查是否已添加过该手机号
            val alreadyExists = existingContacts.any { it.phone == formattedPhone }
            if (alreadyExists) {
                return@withContext Result.Error(null, "该手机号已添加为紧急联系人")
            }

            // 4. 发送短信验证码
            val sendRequest = SmsSendRequest(phone = formattedPhone)
            val sendResult = remoteDataSource.sendSmsCode(sendRequest)
            if (!sendResult.isSuccess) {
                val e = sendResult.exceptionOrNull()
                return@withContext Result.Error(e, "发送验证码失败: ${e?.message}")
            }

            // 5. 验证短信验证码
            val verifyRequest = SmsVerifyRequest(phone = formattedPhone, code = params.smsCode)
            val verifyResult = remoteDataSource.verifySmsCode(verifyRequest)
            if (verifyResult.isSuccess) {
                val verified = verifyResult.getOrNull()?.verified ?: false
                if (!verified) {
                    return@withContext Result.Error(null, "验证码错误")
                }
            } else {
                val e = verifyResult.exceptionOrNull()
                return@withContext Result.Error(e, "验证码校验失败: ${e?.message}")
            }

            // 6. 手机号验证通过，保存联系人
            val contact = EmergencyContact(
                userId = params.userId,
                name = params.name.trim(),
                relationship = params.relationship,
                phone = formattedPhone,
                isVerified = true
            )

            val saveResult = repository.add(contact)
            if (saveResult.isSuccess) {
                val saved = saveResult.getOrNull() ?: contact
                Result.Success(saved)
            } else {
                val e = saveResult.exceptionOrNull()
                Result.Error(e, "保存联系人失败: ${e?.message}")
            }
        }
    }
}
