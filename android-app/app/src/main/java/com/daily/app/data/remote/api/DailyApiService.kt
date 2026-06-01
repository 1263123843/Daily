package com.daily.app.data.remote.api

import com.daily.app.data.remote.api.model.*
import retrofit2.http.*

/**
 * Daily 后端 API 服务接口.
 *
 * 定义所有与后端交互的 REST API 端点，
 * 使用 Retrofit 注解声明 HTTP 方法和路径。
 *
 * 基础 URL 由 Retrofit.Builder.baseUrl() 设置，
 * 所有路径均为相对于 baseUrl 的相对路径。
 */
interface DailyApiService {

    /**
     * 用户签到.
     */
    @POST("checkin")
    suspend fun checkin(
        @Body request: CheckinRequest
    ): ApiResponse<CheckinResponse>

    /**
     * 获取当前用户状态信息（含连续签到天数、最后签到时间等）.
     */
    @GET("users/me/status")
    suspend fun getUserStatus(): ApiResponse<UserStatusResponse>

    /**
     * 获取用户签到历史（分页）.
     *
     * @param page 页码，从 1 开始
     * @param pageSize 每页数量
     */
    @GET("users/me/checkins")
    suspend fun getCheckinHistory(
        @Query("page") page: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): ApiResponse<PaginatedResponse<CheckinResponse>>

    /**
     * 添加紧急联系人.
     */
    @POST("contacts")
    suspend fun createContact(
        @Body request: ContactRequest
    ): ApiResponse<ContactResponse>

    /**
     * 删除紧急联系人.
     *
     * @param id 联系人 ID
     */
    @DELETE("contacts/{id}")
    suspend fun deleteContact(
        @Path("id") id: Long
    ): ApiResponse<Unit>

    /**
     * 发送短信验证码.
     */
    @POST("sms/send-code")
    suspend fun sendSmsCode(
        @Body request: SmsSendRequest
    ): ApiResponse<Unit>

    /**
     * 验证短信验证码.
     */
    @POST("sms/verify")
    suspend fun verifySmsCode(
        @Body request: SmsVerifyRequest
    ): ApiResponse<SmsVerifyResponse>
}
