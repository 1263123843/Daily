package com.daily.app.di

import com.daily.app.BuildConfig
import com.daily.app.data.remote.api.DailyApiService
import com.daily.app.data.remote.interceptor.JwtAuthInterceptor
import com.daily.app.data.remote.interceptor.MockNetworkInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * 网络模块 - 提供 OkHttp Client、Retrofit 实例和 API Service.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideJwtAuthInterceptor(): JwtAuthInterceptor {
        return JwtAuthInterceptor()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        jwtAuthInterceptor: JwtAuthInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

        return if (BuildConfig.MOCK_MODE) {
            // Mock 模式：使用 Mock 拦截器，所有请求返回模拟数据
            builder.addInterceptor(MockNetworkInterceptor()).build()
        } else {
            // 正常模式：使用 JWT 认证拦截器
            builder.addInterceptor(jwtAuthInterceptor).build()
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideDailyApiService(retrofit: Retrofit): DailyApiService {
        return retrofit.create(DailyApiService::class.java)
    }
}
