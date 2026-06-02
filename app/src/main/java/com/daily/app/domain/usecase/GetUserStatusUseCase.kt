package com.daily.app.domain.usecase

import com.daily.app.domain.model.UserStatus
import com.daily.app.domain.repository.ICheckinRepository
import com.daily.app.util.Result
import com.daily.app.data.remote.CheckinRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 获取用户状态用例.
 *
 * 业务流程:
 * 1. 调用远程 API 获取用户最新状态
 * 2. 将 API 响应模型转换为领域模型 UserStatus
 * 3. 返回用户状态信息（含连续签到天数、最后签到时间等）
 *
 * @property remoteDataSource 远程数据源（提供 getUserStatus API）
 */
@Singleton
class GetUserStatusUseCase @Inject constructor(
    private val remoteDataSource: CheckinRemoteDataSource
) : IGetUserStatusUseCase {

    override suspend operator fun invoke(): Result<UserStatus> {
        return withContext(Dispatchers.IO) {
            val result = remoteDataSource.getUserStatus()
            if (result.isSuccess) {
                val response = result.getOrNull() ?: return@withContext Result.Error(
                    null, "用户状态数据为空"
                )
                val status = UserStatus(
                    userId = response.userId,
                    nickname = response.nickname,
                    status = response.status,
                    lastCheckinTime = response.lastCheckinTime,
                    lastCheckinAgoMinutes = response.lastCheckinAgoMinutes,
                    consecutiveDays = response.consecutiveDays,
                    emergencyContactsCount = response.emergencyContactsCount
                )
                Result.Success(status)
            } else {
                val e = result.exceptionOrNull()
                Result.Error(e, "获取用户状态失败: ${e?.message}")
            }
        }
    }
}
