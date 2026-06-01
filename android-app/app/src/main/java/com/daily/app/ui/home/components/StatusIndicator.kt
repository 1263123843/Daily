package com.daily.app.ui.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.daily.app.ui.home.SafetyStatus
import com.daily.app.ui.theme.Danger
import com.daily.app.ui.theme.Safe
import com.daily.app.ui.theme.Warning

/**
 * 安全状态指示器 — 大圆形 + 状态文字.
 *
 * @param status 安全状态
 * @param modifier 修饰符
 */
@Composable
fun StatusIndicator(
    status: SafetyStatus,
    modifier: Modifier = Modifier
) {
    val (color, statusText) = when (status) {
        SafetyStatus.SAFE -> Safe to "安全"
        SafetyStatus.WARNING -> Warning to "警告"
        SafetyStatus.DANGER -> Danger to "异常"
    }

    // 脉冲动画（仅安全状态）
    val scale = remember { Animatable(1f) }
    LaunchedEffect(status) {
        if (status == SafetyStatus.SAFE) {
            while (true) {
                scale.animateTo(1.1f, tween(800))
                scale.animateTo(1f, tween(800))
            }
        } else {
            scale.snapTo(1f)
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f))
                .then(if (status == SafetyStatus.SAFE) Modifier.size(120.dp * scale.value) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }

        Text(
            text = statusText,
            style = MaterialTheme.typography.headlineMedium,
            color = color
        )
    }
}
