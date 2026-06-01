package com.daily.app.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.daily.app.ui.theme.Primary

/**
 * 引导第一步 — 应用简介.
 *
 * 显示应用 Logo/图标和简介文字 "Daily — 你的安全守护".
 */
@Composable
fun OnboardingIntroStep(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Primary,
            modifier = Modifier.size(100.dp)
        )

        Text(
            text = "Daily",
            style = MaterialTheme.typography.displaySmall,
            color = Primary,
            modifier = Modifier.padding(top = 32.dp, bottom = 4.dp)
        )

        Text(
            text = "你的安全守护",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text(
            text = "独居安全签到应用，解锁即签到。\n每天自动记录你的安全状态，\n让关心你的人放心。",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
