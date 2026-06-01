package com.daily.app.domain.usecase

import com.daily.app.domain.model.Checkin
import com.daily.app.domain.model.CheckinSource
import com.daily.app.domain.repository.ICheckinRepository
import com.daily.app.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 执行签到用例.
 *
 * 业务流程:
 * 1. 获取设备信息，构建签到领域模型
 * 2. 通过 Repository 保存到本地数据库（离线优先）
 * 3. Repository 内部会尝试上传到服务器
 * 4. 如果网络失败，签到记录保留在本地待同步队列
 *
 * @property repository 签到仓库
 */
@Singleton
class CheckinUseCase @Inject constructor(
    private val repository: ICheckinRepository
) : ICheckinUseCase {

    override suspend operator fun invoke(params: CheckinParams): Result<Checkin> {
        return withContext(Dispatchers.IO) {
            try {
                val checkin = Checkin(
                    userId = params.userId,
                    checkinTime = Instant.now(),
                    latitude = params.latitude,
                    longitude = params.longitude,
                    source = params.source,
                    deviceId = params.deviceId
                )

                val saveResult = repository.save(checkin)
                if (saveResult.isSuccess) {
                    Result.Success(saveResult.getOrNull() ?: checkin)
                } else {
                    Result.Error(saveResult.exceptionOrNull(), "签到失败: ${saveResult.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                Result.Error(e, "签到失败")
            }
        }
    }

    companion object {
        /** 手动签到来源 */
        val SOURCE_MANUAL = CheckinSource.MANUAL
    }
}
