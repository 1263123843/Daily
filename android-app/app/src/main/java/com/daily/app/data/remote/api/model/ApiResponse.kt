package com.daily.app.data.remote.api.model

import com.google.gson.annotations.SerializedName

/**
 * API 通用响应包装类.
 *
 * 后端统一响应格式: { code, data, message }
 * - code == 0 表示成功
 * - code != 0 表示业务错误
 *
 * @property T 响应数据的具体类型
 * @property code 状态码，0=成功
 * @property data 响应数据体（可能为 null）
 * @property message 响应消息
 */
data class ApiResponse<T>(
    @SerializedName("code")
    val code: Int,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("message")
    val message: String = ""
) {
    /**
     * 判断请求是否成功.
     */
    val isSuccess: Boolean get() = code == 0
}

/**
 * API 错误响应模型.
 *
 * 用于解析非成功的 API 响应中的错误详情。
 *
 * @property code 错误码
 * @property message 错误信息
 * @property detail 详细错误描述（可选）
 */
data class ErrorResponse(
    @SerializedName("code")
    val code: Int,

    @SerializedName("message")
    val message: String,

    @SerializedName("detail")
    val detail: String? = null
)

/**
 * 分页响应包装类.
 *
 * 用于包装分页查询的返回结果。
 *
 * @property T 列表项类型
 * @property items 当前页数据列表
 * @property total 总记录数
 * @property page 当前页码
 * @property pageSize 每页大小
 * @property totalPages 总页数
 */
data class PaginatedResponse<T>(
    @SerializedName("items")
    val items: List<T> = emptyList(),

    @SerializedName("total")
    val total: Int = 0,

    @SerializedName("page")
    val page: Int = 1,

    @SerializedName("pageSize")
    val pageSize: Int = 20,

    @SerializedName("totalPages")
    val totalPages: Int = 0
)
