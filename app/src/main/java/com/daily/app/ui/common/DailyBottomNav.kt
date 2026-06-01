package com.daily.app.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.daily.app.ui.navigation.DailyScreen
import com.daily.app.ui.theme.Primary

/** 底部导航项数据类 */
data class BottomNavItem(
    val screen: DailyScreen,
    val label: String,
    val icon: ImageVector
)

private val BottomNavItems = listOf(
    BottomNavItem(DailyScreen.Home, "首页", Icons.Default.Home),
    BottomNavItem(DailyScreen.Wallpaper, "壁纸", Icons.Default.FavoriteBorder),
    BottomNavItem(DailyScreen.Contacts, "联系人", Icons.Default.Person),
    BottomNavItem(DailyScreen.Settings, "设置", Icons.Default.Settings)
)

/**
 * Daily App 底部导航栏.
 *
 * @param visible 是否显示底部导航
 * @param currentRoute 当前路由
 * @param onNavigate 导航回调
 * @param content 导航栏上方的内容
 */
@Composable
fun DailyBottomNav(
    visible: Boolean,
    currentRoute: String,
    onNavigate: (DailyScreen) -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        content()

        if (visible) {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.height(60.dp)
            ) {
                BottomNavItems.forEach { item ->
                    val isSelected = currentRoute == item.screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { onNavigate(item.screen) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            indicatorColor = Primary.copy(alpha = 0.1f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}
