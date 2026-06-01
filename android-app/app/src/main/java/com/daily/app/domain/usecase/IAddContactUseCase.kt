package com.daily.app.domain.usecase

import com.daily.app.domain.model.EmergencyContact
import com.daily.app.domain.model.Relationship
import com.daily.app.util.Result

/**
 * 添加联系人用例接口.
 */
interface IAddContactUseCase {
    /**
     * 添加紧急联系人.
     *
     * @param params 联系人参数
     * @return 添加结果
     */
    suspend operator fun invoke(params: AddContactParams): Result<EmergencyContact>
}

/**
 * 添加联系人参数.
 *
 * @property userId 用户 ID
 * @property name 联系人姓名
 * @property relationship 关系类型
 * @property phone 手机号（明文）
 * @property smsCode 短信验证码
 */
data class AddContactParams(
    val userId: String,
    val name: String,
    val relationship: Relationship,
    val phone: String,
    val smsCode: String
)
