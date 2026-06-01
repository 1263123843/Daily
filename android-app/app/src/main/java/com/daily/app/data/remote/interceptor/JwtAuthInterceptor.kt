package com.daily.app.data.remote.interceptor

import com.daily.app.data.preferences.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * JWT 认证拦截器.
 *
 * 自动从 DataStore 读取 JWT Token 并添加到每个请求的 Authorization Header 中。
 *
 * 使用 runBlocking 在同步的 intercept 方法中读取 DataStore（因为 DataStore 是异步的）。
 * 这在 OkHttp 拦截器中是可接受的用法，因为 DataStore 内部有缓存，读取速度很快。
 */
@Singleton
class JwtAuthInterceptor @Inject constructor(
    private val userPreferences: UserPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = runBlocking { userPreferences.jwtToken.first() }

        if (!token.isNullOrEmpty()) {
            val authorizedRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            return chain.proceed(authorizedRequest)
        }

        return chain.proceed(originalRequest)
    }
}
