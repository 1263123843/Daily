package com.daily.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dagger.hilt.android.AndroidEntryPoint
import com.daily.app.data.preferences.UserPreferences
import com.daily.app.ui.theme.DailyTheme
import com.daily.app.ui.navigation.DailyNavHost
import javax.inject.Inject

/**
 * Daily App 主 Activity.
 *
 * 应用入口点，负责：
 * 1. 初始化 Compose UI
 * 2. 监听 onboarding 完成状态
 * 3. 将导航状态传递给 DailyNavHost
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DailyTheme {
                val onboardingCompleted by userPreferences.isOnboardingCompleted.collectAsState(
                    initialValue = false
                )
                DailyNavHost(onboardingCompleted = onboardingCompleted)
            }
        }
    }
}
