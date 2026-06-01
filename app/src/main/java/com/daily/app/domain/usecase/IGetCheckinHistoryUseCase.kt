package com.daily.app.domain.usecase

import com.daily.app.domain.model.Checkin
import com.daily.app.util.Result

/**
 * 获取签到历史用例接口.
 */
interface IGetCheckinHistoryUseCase {
    /**
     * 获取分页签到历史.
     *
     * @param params 分页参数
     * @return 签到记录列表结果
     */
    suspend operator fun invoke(params: CheckinHistoryParams): Result<List<Checkin>>
}

/**
 * 签到历史查询参数.
 *
 * @property page 页码，从 1 开始
 * @property pageSize 每页数量
 */
data class CheckinHistoryParams(
    val page: Int = 1,
    val pageSize: Int = 20
)
