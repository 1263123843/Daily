package com.daily.app.domain.usecase

import com.daily.app.util.Result

/**
 * 同步签到用例接口.
 */
interface ISyncCheckinsUseCase {
    /**
     * 同步待上传的签到记录.
     *
     * @param params 同步参数（包含 userId）
     * @return 同步结果，data 为成功同步的记录数
     */
    suspend operator fun invoke(params: SyncCheckinsParams): Result<Int>
}

/**
 * 同步签到参数.
 *
 * @property userId 用户 ID
 */
data class SyncCheckinsParams(
    val userId: String
)
