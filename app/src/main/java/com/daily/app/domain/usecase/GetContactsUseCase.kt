package com.daily.app.domain.usecase

import com.daily.app.domain.model.EmergencyContact
import com.daily.app.domain.repository.IContactRepository
import com.daily.app.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 获取联系人列表用例.
 *
 * 业务流程:
 * 1. 从本地缓存获取用户所有紧急联系人
 * 2. 返回联系人列表
 *
 * 注意: 本地缓存中的联系人手机号为加密存储，
 * 领域模型中 phone 字段为空字符串（不暴露明文）。
 *
 * @property repository 联系人仓库
 */
@Singleton
class GetContactsUseCase @Inject constructor(
    private val repository: IContactRepository
) : IGetContactsUseCase {

    override suspend operator fun invoke(params: GetContactsParams): Result<List<EmergencyContact>> {
        return withContext(Dispatchers.IO) {
            try {
                val contacts = repository.getAllByUserIdSync(params.userId)
                Result.Success(contacts)
            } catch (e: Exception) {
                Result.Error(e, "获取联系人失败")
            }
        }
    }
}
