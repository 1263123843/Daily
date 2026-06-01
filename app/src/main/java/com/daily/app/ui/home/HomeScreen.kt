package com.daily.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daily.app.ui.common.EmptyState
import com.daily.app.ui.common.ErrorView
import com.daily.app.ui.common.LoadingIndicator
import com.daily.app.ui.home.components.LastCheckinCard
import com.daily.app.ui.home.components.StatusIndicator
import com.daily.app.ui.theme.Primary
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

/**
 * 首页 — 安全状态仪表盘.
 *
 * @param viewModel 首页 ViewModel
 * @param modifier 修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Daily",
                        fontWeight = FontWeight.W700,
                        color = Primary
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        when {
            uiState.loading && uiState.lastCheckinTime == null -> {
                LoadingIndicator(modifier = Modifier.padding(padding))
            }
            uiState.error != null && uiState.lastCheckinTime == null -> {
                ErrorView(
                    errorMessage = uiState.error!!,
                    onRetry = { viewModel.loadStatus() },
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                val pullState = rememberPullToRefreshState()
                PullToRefreshBox(
                    state = pullState,
                    isRefreshing = uiState.loading,
                    onRefresh = { viewModel.loadStatus() },
                    modifier = Modifier.padding(padding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // 安全状态指示器
                        StatusIndicator(status = uiState.safetyStatus)

                        // 上次签到卡片
                        LastCheckinCard(
                            lastCheckinTime = uiState.lastCheckinTime,
                            consecutiveDays = uiState.consecutiveDays
                        )

                        // 错误信息提示
                        if (uiState.error != null && uiState.lastCheckinTime != null) {
                            Text(
                                text = uiState.error!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        // 快速操作按钮
                        QuickActions(
                            contactCount = uiState.contactCount,
                            checkinLoading = uiState.checkinLoading,
                            onManualCheckin = { viewModel.manualCheckin() }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 快速操作区域.
 */
@Composable
private fun QuickActions(
    contactCount: Int,
    checkinLoading: Boolean,
    onManualCheckin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 手动签到按钮
        Button(
            onClick = onManualCheckin,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            enabled = !checkinLoading
        ) {
            if (checkinLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(all = 8.dp),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("手动签到")
            }
        }

        // 联系人状态提示
        if (contactCount == 0) {
            Text(
                text = "建议添加紧急联系人以保障安全",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Text(
                text = "已添加 $contactCount 位紧急联系人",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
