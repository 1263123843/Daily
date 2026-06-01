package com.daily.app.ui.navigation

/**
 * Daily App 所有页面路由密封类.
 *
 * 使用密封类确保路由类型安全，避免字符串硬编码错误。
 */
sealed class DailyScreen(val route: String) {
    /** 引导页 */
    object Onboarding : DailyScreen("onboarding")

    /** 首页（安全状态仪表盘） */
    object Home : DailyScreen("home")

    /** 壁纸设置 */
    object Wallpaper : DailyScreen("wallpaper")

    /** 紧急联系人管理 */
    object Contacts : DailyScreen("contacts")

    /** 应用设置 */
    object Settings : DailyScreen("settings")
}
