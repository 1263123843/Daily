package com.daily.app.data.remote

import com.daily.app.data.remote.api.DailyApiService
import com.daily.app.data.remote.api.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 签到远程数据源.
 *
 * 封装 Retrofit API 调用，提供统一的异常处理和 Result 类型返回。
 *
 * @property apiService Daily API 服务接口实例
 */
@Singleton
class CheckinRemoteDataSource @Inject constructor(
    private val apiService: DailyApiService
) {

    /**
     * 调用签到 API.
     *
     * 成功时返回 CheckinResponse，失败时返回 Result.failure 并携带异常信息。
     */
    suspend fun checkin(request: CheckinRequest): Result<CheckinResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.checkin(request)
                if (response.isSuccess && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(ApiException(response.code, response.message))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "网络请求失败", e))
            }
        }
    }

    /**
     * 获取用户状态.
     */
    suspend fun getUserStatus(): Result<UserStatusResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getUserStatus()
                if (response.isSuccess && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(ApiException(response.code, response.message))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "网络请求失败", e))
            }
        }
    }

    /**
     * 获取签到历史（分页）.
     */
    suspend fun getCheckinHistory(
        page: Int = 1,
        pageSize: Int = 20
    ): Result<PaginatedResponse<CheckinResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCheckinHistory(page, pageSize)
                if (response.isSuccess && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(ApiException(response.code, response.message))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "网络请求失败", e))
            }
        }
    }

    // --- Contact 相关远程操作 ---

    /**
     * 创建联系人（通过 Remote DataSource 统一管理，供 Repository 使用）.
     */
    suspend fun createContact(request: ContactRequest): Result<ContactResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createContact(request)
                if (response.isSuccess && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(ApiException(response.code, response.message))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "网络请求失败", e))
            }
        }
    }

    /**
     * 删除联系人.
     */
    suspend fun deleteContact(id: Long): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.deleteContact(id)
                if (response.isSuccess) {
                    Result.success(Unit)
                } else {
                    Result.failure(ApiException(response.code, response.message))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "网络请求失败", e))
            }
        }
    }

    /**
     * 发送短信验证码.
     */
    suspend fun sendSmsCode(request: SmsSendRequest): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.sendSmsCode(request)
                if (response.isSuccess) {
                    Result.success(Unit)
                } else {
                    Result.failure(ApiException(response.code, response.message))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "网络请求失败", e))
            }
        }
    }

    /**
     * 验证短信验证码.
     */
    suspend fun verifySmsCode(request: SmsVerifyRequest): Result<SmsVerifyResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.verifySmsCode(request)
                if (response.isSuccess && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(ApiException(response.code, response.message))
                }
            } catch (e: Exception) {
                Result.failure(NetworkException(e.message ?: "网络请求失败", e))
            }
        }
    }
}

// --- 异常类定义 ---

/**
 * API 业务异常.
 *
 * 当后端返回 code != 0 时抛出。
 *
 * @property code 错误码
 * @property message 错误消息
 */
class ApiException(val code: Int, override val message: String) : Exception(message)

/**
 * 网络异常.
 *
 * 封装所有网络层相关的异常。
 *
 * @property message 错误描述
 * @property cause 原始异常
 */
class NetworkException(override val message: String, override val cause: Throwable? = null) : Exception(message, cause)
