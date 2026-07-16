package org.example.project.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.domain.model.UserSettings
import org.example.project.domain.usecase.settings.GetUserSettingsUseCase
import org.example.project.domain.usecase.settings.UpdateUserSettingsUseCase

data class SettingsUiState(
    val isLoading: Boolean = true,
    val settings: UserSettings = UserSettings(),
    val error: String? = null
)

sealed interface SettingsEffect {
    data object Saved : SettingsEffect
    data object Reset : SettingsEffect
}

class SettingsViewModel(
    private val getSettings: GetUserSettingsUseCase,
    private val updateSettings: UpdateUserSettingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _effect = Channel<SettingsEffect>(Channel.BUFFERED)
    val effect: Flow<SettingsEffect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            getSettings().collect { s ->
                _uiState.update { it.copy(isLoading = false, settings = s) }
            }
        }
    }

    fun updateCurrency(symbol: String) = viewModelScope.launch {
        updateSettings.updateCurrency(symbol)
        _effect.send(SettingsEffect.Saved)
    }

    fun updateDarkTheme(isDark: Boolean) = viewModelScope.launch {
        updateSettings.updateDarkTheme(isDark)
    }

    fun updateFirstDayOfWeek(day: Int) = viewModelScope.launch {
        updateSettings.updateFirstDayOfWeek(day)
    }

    fun updateMonthlyBudget(amount: Double) = viewModelScope.launch {
        updateSettings.updateMonthlyBudget(amount)
        _effect.send(SettingsEffect.Saved)
    }

    fun updateEmergencyMonths(months: Int) = viewModelScope.launch {
        updateSettings.updateEmergencyMonths(months)
        _effect.send(SettingsEffect.Saved)
    }

    fun updateAges(current: Int, retirement: Int) = viewModelScope.launch {
        updateSettings.updateAges(current, retirement)
        _effect.send(SettingsEffect.Saved)
    }

    fun updateFireMultiplier(multiplier: Int) = viewModelScope.launch {
        updateSettings.updateFireSettings(multiplier)
        _effect.send(SettingsEffect.Saved)
    }

    fun updateReturns(equity: Double, debt: Double, gold: Double, fd: Double) = viewModelScope.launch {
        updateSettings.updateReturns(equity, debt, gold, fd)
        _effect.send(SettingsEffect.Saved)
    }

    fun updateAllocations(equity: Double, debt: Double, gold: Double, fd: Double) = viewModelScope.launch {
        updateSettings.updateAllocations(equity, debt, gold, fd)
        _effect.send(SettingsEffect.Saved)
    }

    fun updateGrowthRates(inflation: Double, salaryGrowth: Double, taxRate: Double) = viewModelScope.launch {
        updateSettings.updateGrowthRates(inflation, salaryGrowth, taxRate)
        _effect.send(SettingsEffect.Saved)
    }

    fun resetToDefaults() = viewModelScope.launch {
        updateSettings.resetToDefaults()
        _effect.send(SettingsEffect.Reset)
    }
}
