package com.daily.app.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * 将 Flow<T> 转换为 Flow<Result<T>>.
 *
 * 在流开始发射时先发出 [Result.Loading]，
 * 正常数据被包装为 [Result.Success]，
 * 异常被捕获并包装为 [Result.Error]。
 *
 * 示例:
 * ```
 * repository.getItems().asResultFlow()
 *   .collect { result ->
 *       when (result) {
 *           is Result.Loading -> showProgress()
 *           is Result.Success -> showData(result.data)
 *           is Result.Error -> showError(result.message)
 *       }
 *   }
 * ```
 */
fun <T> Flow<T>.asResultFlow(): Flow<Result<T>> = this
    .onStart { emit(Result.Loading) }
    .map { value -> Result.Success(value) }
    .catch { e -> emit(Result.Error(throwable = e)) }
