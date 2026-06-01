package com.daily.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Daily Application 入口类.
 *
 * 使用 @HiltAndroidApp 注解启用 Hilt 依赖注入框架。
 * 所有使用 Hilt 的组件（Activity、Service、Receiver 等）都需要此 Application 类。
 */
@HiltAndroidApp
class DailyApplication : Application()
