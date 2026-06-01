package com.daily.app.domain.usecase

import com.daily.app.domain.repository.IContactRepository
import com.daily.app.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 删除紧急联系人用例.
 *
 * 业务流程:
 * 1. 调用 Repository 删除联系人（Repository 内部会先调 API 删除，成功后再删本地缓存）
 * 2. 返回删除结果
 *
 * @property repository 联系人仓库
 */
@Singleton
class DeleteContactUseCase @Inject constructor(
    private val repository: IContactRepository
) : IDeleteContactUseCase {

    override suspend operator fun invoke(params: DeleteContactParams): Result<Unit> {
        return withContext(Dispatchers.IO) {
            when (val result = repository.delete(params.contactId, params.userId)) {
                is Result.Success -> Result.Success(Unit)
                is Result.Failure -> {
                    val e = result.exceptionOrNull()
                    Result.Error(e, "删除联系人失败: ${e?.message}")
                }
            }
        }
    }
}
