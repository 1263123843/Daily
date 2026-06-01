package com.daily.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.daily.app.data.preferences.UserPreferences
import com.daily.app.ui.common.DailyBottomNav
import com.daily.app.ui.contacts.ContactsScreen
import com.daily.app.ui.home.HomeScreen
import com.daily.app.ui.onboarding.OnboardingScreen
import com.daily.app.ui.settings.SettingsScreen
import com.daily.app.ui.wallpaper.WallpaperScreen
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

/**
 * Daily App 主导航入口.
 *
 * 根据 onboarding 完成状态决定显示引导流程或主导航。
 *
 * @param onboardingCompleted 是否已完成引导
 * @param modifier 修饰符
 * @param navController 导航控制器
 */
@Composable
fun DailyNavHost(
    onboardingCompleted: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomNav = currentRoute in listOf(
        DailyScreen.Home.route,
        DailyScreen.Wallpaper.route,
        DailyScreen.Contacts.route,
        DailyScreen.Settings.route
    )

    DailyBottomNav(
        visible = showBottomNav,
        currentRoute = currentRoute ?: DailyScreen.Home.route,
        onNavigate = { screen ->
            navController.navigate(screen.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = if (onboardingCompleted) DailyScreen.Home.route else DailyScreen.Onboarding.route,
            modifier = modifier
        ) {
            composable(DailyScreen.Onboarding.route) {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate(DailyScreen.Home.route) {
                            popUpTo(DailyScreen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(DailyScreen.Home.route) {
                HomeScreen()
            }
            composable(DailyScreen.Wallpaper.route) {
                WallpaperScreen()
            }
            composable(DailyScreen.Contacts.route) {
                ContactsScreen()
            }
            composable(DailyScreen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
