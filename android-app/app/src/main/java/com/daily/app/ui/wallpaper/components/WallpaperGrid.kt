package com.daily.app.ui.wallpaper.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daily.app.ui.theme.Primary
import com.daily.app.ui.wallpaper.WallpaperPreset

/**
 * 壁纸网格 — 展示预设壁纸选项.
 *
 * @param presets 预设壁纸列表
 * @param selectedId 当前选中的壁纸 ID
 * @param onSelect 选择回调
 * @param modifier 修饰符
 */
@Composable
fun WallpaperGrid(
    presets: List<WallpaperPreset>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        items(presets, key = { it.id }) { preset ->
            val isSelected = selectedId == preset.id
            val presetColor = hexToColor(preset.colorHex)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .aspectRatio(1.2f)
                    .clip(RoundedCornerShape(12.dp))
                    .then(
                        if (isSelected) {
                            Modifier.border(
                                3.dp,
                                Primary,
                                RoundedCornerShape(12.dp)
                            )
                        } else {
                            Modifier
                        }
                    )
                    .clickable { onSelect(preset.id) },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(presetColor)
                ) {
                    Text(
                        text = preset.name,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

/**
 * 将十六进制颜色字符串转换为 Compose Color.
 * 例如 "#FF7043" -> Color(0xFFFF7043)
 */
private fun hexToColor(hex: String): Color {
    val clean = hex.trim().trimStart('#')
    return when (clean.length) {
        6 -> Color(androidxComposeUiParseHex("FF$clean"))
        8 -> Color(androidxComposeUiParseHex(clean))
        else -> Color.Gray
    }
}

private fun androidxComposeUiParseHex(hex: String): Long {
    return hex.toLong(16)
}
