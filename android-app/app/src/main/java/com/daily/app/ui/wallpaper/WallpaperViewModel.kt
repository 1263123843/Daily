package com.daily.app.ui.wallpaper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.app.data.preferences.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 壁纸预设选项数据类.
 */
data class WallpaperPreset(
    val id: String,
    val name: String,
    val colorHex: String
)

/**
 * 壁纸页面 ViewModel.
 *
 * 负责管理壁纸选择和保存。
 */
@HiltViewModel
class WallpaperViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _selectedWallpaper = MutableStateFlow<String?>(null)
    val selectedWallpaper: StateFlow<String?> = _selectedWallpaper.asStateFlow()

    val presetWallpapers = listOf(
        WallpaperPreset("orange", "活力橙", "#FF7043"),
        WallpaperPreset("green", "安全绿", "#66BB6A"),
        WallpaperPreset("blue", "宁静蓝", "#5C6BC0"),
        WallpaperPreset("pink", "温馨粉", "#EC407A"),
        WallpaperPreset("purple", "梦幻紫", "#AB47BC"),
        WallpaperPreset("teal", "清新青", "#26A69A")
    )

    init {
        loadCurrentWallpaper()
    }

    /**
     * 加载当前壁纸.
     */
    private fun loadCurrentWallpaper() {
        viewModelScope.launch {
            userPreferences.currentWallpaperUri.collect { uri ->
                _selectedWallpaper.value = uri
            }
        }
    }

    /**
     * 选择壁纸.
     */
    fun selectWallpaper(wallpaperId: String?) {
        viewModelScope.launch {
            _selectedWallpaper.value = wallpaperId
            userPreferences.setCurrentWallpaperUri(wallpaperId)
        }
    }
}
