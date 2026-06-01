package com.daily.app.domain.usecase

import com.daily.app.util.Result

/**
 * 删除联系人用例接口.
 */
interface IDeleteContactUseCase {
    /**
     * 删除紧急联系人.
     *
     * @param params 删除参数（contactId 和 userId）
     * @return 删除结果
     */
    suspend operator fun invoke(params: DeleteContactParams): Result<Unit>
}

/**
 * 删除联系人参数.
 *
 * @property contactId 联系人 ID
 * @property userId 用户 ID
 */
data class DeleteContactParams(
    val contactId: Long,
    val userId: String
)
