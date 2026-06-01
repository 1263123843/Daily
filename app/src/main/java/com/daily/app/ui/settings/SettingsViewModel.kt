package com.daily.app.ui.settings

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
 * 设置页面 UI 状态.
 */
data class SettingsUiState(
    val nickname: String = "",
    val darkModeEnabled: Boolean = false,
    val loading: Boolean = false,
    val clearDataConfirmed: Boolean = false
)

/**
 * 设置页面 ViewModel.
 *
 * 负责管理用户昵称、深色模式切换、清除数据等设置操作。
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    /**
     * 加载设置信息.
     */
    private fun loadSettings() {
        viewModelScope.launch {
            userPreferences.nickname.collect { nick ->
                _uiState.update { it.copy(nickname = nick ?: "") }
            }
        }
    }

    /**
     * 更新用户昵称.
     *
     * @param name 新昵称
     */
    fun updateNickname(name: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(nickname = name) }
            userPreferences.setNickname(name)
        }
    }

    /**
     * 切换深色模式.
     *
     * @param enabled 是否启用深色模式
     */
    fun toggleDarkMode(enabled: Boolean) {
        _uiState.update { it.copy(darkModeEnabled = enabled) }
    }

    /**
     * 显示清除数据确认.
     */
    fun showClearDataConfirmation() {
        _uiState.update { it.copy(clearDataConfirmed = true) }
    }

    /**
     * 取消清除数据.
     */
    fun dismissClearDataConfirmation() {
        _uiState.update { it.copy(clearDataConfirmed = false) }
    }

    /**
     * 清除所有用户数据.
     */
    fun clearAllData() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            try {
                userPreferences.clearAll()
                _uiState.update { it.copy(loading = false, clearDataConfirmed = false, nickname = "") }
            } catch (e: Exception) {
                _uiState.update { it.copy(loading = false) }
            }
        }
    }
}
