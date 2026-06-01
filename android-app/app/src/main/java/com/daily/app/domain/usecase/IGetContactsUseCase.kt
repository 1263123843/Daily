package com.daily.app.domain.usecase

import com.daily.app.domain.model.EmergencyContact
import com.daily.app.util.Result

/**
 * 获取联系人列表用例接口.
 */
interface IGetContactsUseCase {
    /**
     * 获取用户所有紧急联系人.
     *
     * @param params 查询参数（userId）
     * @return 联系人列表结果
     */
    suspend operator fun invoke(params: GetContactsParams): Result<List<EmergencyContact>>
}

/**
 * 获取联系人参数.
 *
 * @property userId 用户 ID
 */
data class GetContactsParams(
    val userId: String
)
