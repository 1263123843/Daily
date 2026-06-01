package com.daily.app.util

/**
 * 统一的结果状态密封类.
 *
 * 用于 UseCase 层向 UI 层暴露操作结果，包含成功、错误和加载中三种状态。
 * 与 kotlin.Result 不同，此类型额外提供 Loading 状态以支持 UI 层的加载指示器。
 *
 * @property T 成功时携带的数据类型
 */
sealed class Result<out T> {

    /**
     * 操作成功.
     *
     * @property data 成功返回的数据
     */
    data class Success<out T>(val data: T) : Result<T>()

    /**
     * 操作失败.
     *
     * @property throwable 导致失败的异常（可选）
     * @property message 用户可见的错误描述（可选）
     */
    data class Error(val throwable: Throwable? = null, val message: String? = null) : Result<Nothing>()

    /**
     * 操作正在执行中.
     */
    object Loading : Result<Nothing>()
}
