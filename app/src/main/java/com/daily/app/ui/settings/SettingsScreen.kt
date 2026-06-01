package com.daily.app.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * 设置页面 — 用户偏好设置.
 *
 * @param viewModel 设置 ViewModel
 * @param modifier 修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "设置",
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        val contentPadding = padding.calculateTopPadding()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = contentPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // 昵称设置
            NicknameSettingCard(
                nickname = uiState.nickname,
                onNicknameChange = { viewModel.updateNickname(it) }
            )

            Divider()

            // 深色模式开关
            SettingItemRow(
                icon = Icons.Default.DarkMode,
                title = "深色模式",
                subtitle = "跟随系统主题设置",
                trailing = {
                    Switch(
                        checked = uiState.darkModeEnabled,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )
                }
            )

            Divider()

            // 清除数据
            SettingItemRow(
                icon = Icons.Default.DeleteSweep,
                title = "清除所有数据",
                subtitle = "清除本地缓存和偏好设置",
                tint = MaterialTheme.colorScheme.error
            ) {
                IconButton(
                    onClick = { viewModel.showClearDataConfirmation() }
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "清除数据",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Divider()

            // 应用版本
            SettingItemRow(
                icon = Icons.Default.PublishedWithChanges,
                title = "应用版本",
                subtitle = "1.0.0",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    // 清除数据确认对话框
    if (uiState.clearDataConfirmed) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissClearDataConfirmation() },
            title = { Text("清除所有数据") },
            text = {
                Text("确定要清除所有本地数据和偏好设置吗？此操作不可恢复。")
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.clearAllData() },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("清除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissClearDataConfirmation() }) {
                    Text("取消")
                }
            }
        )
    }
}

/**
 * 昵称设置卡片.
 *
 * @param nickname 当前昵称
 * @param onNicknameChange 昵称变更回调
 * @param modifier 修饰符
 */
@Composable
private fun NicknameSettingCard(
    nickname: String,
    onNicknameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var editing by rememberSaveable { mutableStateOf(false) }
    var tempName by rememberSaveable { mutableStateOf(nickname) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { editing = true }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "昵称",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (editing) {
                TextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    maxLines = 1,
                    trailingIcon = {
                        IconButton(onClick = {
                            onNicknameChange(tempName)
                            editing = false
                        }) {
                            Icon(
                                imageVector = Icons.Default.PublishedWithChanges,
                                contentDescription = "保存",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            } else {
                Text(
                    text = nickname.ifEmpty { "点击设置昵称" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (nickname.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * 设置项行布局.
 *
 * @param icon 图标
 * @param title 标题
 * @param subtitle 副标题
 * @param tint 图标颜色
 * @param trailing 右侧内容
 * @param modifier 修饰符
 */
@Composable
private fun SettingItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    tint: Color = MaterialTheme.colorScheme.primary,
    trailing: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(trailing != null) {
                // 点击事件由 trailing 组件处理
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        trailing?.invoke()
    }
}
