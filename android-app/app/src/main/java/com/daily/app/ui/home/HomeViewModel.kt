package com.daily.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.app.data.preferences.UserPreferences
import com.daily.app.domain.model.CheckinSource
import com.daily.app.domain.usecase.ICheckinUseCase
import com.daily.app.domain.usecase.IGetUserStatusUseCase
import com.daily.app.domain.usecase.CheckinParams
import com.daily.app.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 安全状态枚举.
 */
enum class SafetyStatus {
    SAFE,     // 正常 - 最近签到
    WARNING,  // 警告 - 接近签到阈值
    DANGER    // 危险 - 长时间未签到
}

/**
 * 首页 UI 状态.
 */
data class HomeUiState(
    val nickname: String = "用户",
    val lastCheckinTime: String? = null,
    val consecutiveDays: Long = 0L,
    val contactCount: Int = 0,
    val safetyStatus: SafetyStatus = SafetyStatus.DANGER,
    val loading: Boolean = true,
    val error: String? = null,
    val checkinLoading: Boolean = false
)

/**
 * 首页 ViewModel.
 *
 * 负责获取用户状态、执行手动签到等操作。
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserStatusUseCase: IGetUserStatusUseCase,
    private val checkinUseCase: ICheckinUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadStatus()
    }

    /**
     * 加载用户状态信息.
     */
    fun loadStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }

            val nickname = userPreferences.getNickname()

            when (val result = getUserStatusUseCase()) {
                is Result.Success -> {
                    val status = result.data
                    val safetyStatus = when {
                        status.lastCheckinAgoMinutes == null -> SafetyStatus.DANGER
                        status.lastCheckinAgoMinutes < 60 -> SafetyStatus.SAFE
                        status.lastCheckinAgoMinutes < 120 -> SafetyStatus.WARNING
                        else -> SafetyStatus.DANGER
                    }

                    _uiState.update {
                        it.copy(
                            loading = false,
                            nickname = nickname ?: status.nickname ?: "用户",
                            lastCheckinTime = formatRelativeTime(status.lastCheckinAgoMinutes),
                            consecutiveDays = status.consecutiveDays ?: 0L,
                            contactCount = status.emergencyContactsCount ?: 0,
                            safetyStatus = safetyStatus
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            loading = false,
                            error = result.message ?: "获取状态失败",
                            nickname = nickname ?: "用户"
                        )
                    }
                }
                is Result.Loading -> { /* 忽略 */ }
            }
        }
    }

    /**
     * 执行手动签到.
     */
    fun manualCheckin() {
        viewModelScope.launch {
            _uiState.update { it.copy(checkinLoading = true) }

            try {
                val deviceId = userPreferences.getDeviceFingerprint() ?: "unknown"
                val params = CheckinParams(
                    userId = "local_user",
                    latitude = null,
                    longitude = null,
                    source = CheckinSource.MANUAL,
                    deviceId = deviceId
                )

                when (val result = checkinUseCase(params)) {
                    is Result.Success -> {
                        _uiState.update { it.copy(checkinLoading = false) }
                        loadStatus()
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(checkinLoading = false, error = result.message ?: "签到失败")
                        }
                    }
                    is Result.Loading -> { /* 忽略 */ }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(checkinLoading = false, error = "签到失败: ${e.message}")
                }
            }
        }
    }

    /**
     * 格式化相对时间.
     *
     * @param minutes 分钟数
     * @return 格式化后的时间描述
     */
    fun formatRelativeTime(minutes: Long?): String {
        return when {
            minutes == null -> "未知"
            minutes < 1 -> "刚刚"
            minutes < 60 -> "${minutes} 分钟前"
            minutes < 1440 -> "${minutes / 60} 小时前"
            else -> "${minutes / 1440} 天前"
        }
    }
}
