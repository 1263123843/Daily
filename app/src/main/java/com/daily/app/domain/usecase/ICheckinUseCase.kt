package com.daily.app.domain.usecase

import com.daily.app.domain.model.Checkin
import com.daily.app.util.Result

/**
 * 签到用例接口.
 */
interface ICheckinUseCase {
    /**
     * 执行签到操作.
     *
     * @param params 签到参数（userId, latitude, longitude, source, deviceId）
     * @return 签到结果
     */
    suspend operator fun invoke(params: CheckinParams): Result<Checkin>
}

/**
 * 签到参数.
 *
 * @property userId 用户 ID
 * @property latitude 纬度（可选）
 * @property longitude 经度（可选）
 * @property source 签到来源
 * @property deviceId 设备唯一标识
 */
data class CheckinParams(
    val userId: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val source: com.daily.app.domain.model.CheckinSource = com.daily.app.domain.model.CheckinSource.MANUAL,
    val deviceId: String
)
