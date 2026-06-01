package com.daily.app.domain.usecase

import com.daily.app.domain.repository.ICheckinRepository
import com.daily.app.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 同步待上传签到记录用例.
 *
 * 业务流程:
 * 1. 从本地数据库获取所有未同步的签到记录
 * 2. 逐个尝试上传到服务器
 * 3. 上传成功则标记为已同步，失败则跳过（保留在队列中）
 * 4. 返回成功同步的记录数量
 *
 * @property repository 签到仓库
 */
@Singleton
class SyncCheckinsUseCase @Inject constructor(
    private val repository: ICheckinRepository
) : ISyncCheckinsUseCase {

    override suspend operator fun invoke(params: SyncCheckinsParams): Result<Int> {
        return withContext(Dispatchers.IO) {
            try {
                val count = repository.syncPending(params.userId)
                Result.Success(count)
            } catch (e: Exception) {
                Result.Error(e, "同步签到失败")
            }
        }
    }
}
