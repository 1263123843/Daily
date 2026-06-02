package com.daily.app.domain.usecase

import com.daily.app.data.remote.CheckinRemoteDataSource
import com.daily.app.domain.model.Checkin
import com.daily.app.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.format.DateTimeParseException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 获取签到历史用例.
 *
 * 业务流程:
 * 1. 调用远程 API 获取分页签到历史
 * 2. 将 API 响应中的 CheckinResponse 列表转换为 Checkin 领域模型列表
 * 3. 返回签到记录列表
 *
 * 注意: API 返回的 CheckinResponse 不包含 userId、经纬度等字段，
 * 这些字段在领域模型中使用默认值填充。
 *
 * @property remoteDataSource 远程数据源
 */
@Singleton
class GetCheckinHistoryUseCase @Inject constructor(
    private val remoteDataSource: CheckinRemoteDataSource
) : IGetCheckinHistoryUseCase {

    override suspend operator fun invoke(params: CheckinHistoryParams): Result<List<Checkin>> {
        return withContext(Dispatchers.IO) {
            val result = remoteDataSource.getCheckinHistory(params.page, params.pageSize)
            if (result.isSuccess) {
                val paginated = result.getOrNull() ?: return@withContext Result.Error(
                    null, "签到历史数据为空"
                )
                val checkins = paginated.items.map { response ->
                    Checkin(
                        checkinId = response.checkinId,
                        userId = "", // API 响应中不包含 userId，由上层补充
                        checkinTime = parseInstant(response.checkinTime),
                        latitude = null,
                        longitude = null,
                        source = when (response.status?.lowercase()) {
                            "auto" -> com.daily.app.domain.model.CheckinSource.AUTO_UNLOCK
                            else -> com.daily.app.domain.model.CheckinSource.MANUAL
                        },
                        deviceId = null
                    )
                }
                Result.Success(checkins)
            } else {
                val e = result.exceptionOrNull()
                Result.Error(e, "获取签到历史失败: ${e?.message}")
            }
        }
    }

    /**
     * 将 ISO 8601 时间字符串解析为 Instant.
     * 解析失败时返回当前时间作为兜底。
     */
    private fun parseInstant(timeString: String): Instant {
        return try {
            Instant.parse(timeString)
        } catch (_: DateTimeParseException) {
            Instant.now()
        }
    }
}
