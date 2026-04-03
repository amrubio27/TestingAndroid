package com.amrubio27.cursotestingandroid.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amrubio27.cursotestingandroid.core.domain.model.ThemeMode
import com.amrubio27.cursotestingandroid.productlist.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        loadSettings()
    }

    private fun loadSettings() {
        combine(
            settingsRepository.inStockOnly, settingsRepository.themeMode
        ) { inStockOnly, themeMode ->
            _uiState.value = SettingsUiState(inStockOnly, themeMode)
        }.launchIn(viewModelScope)
    }

    fun setInStockOnly(newState: Boolean) {
        viewModelScope.launch {
            settingsRepository.setInStockOnly(newState)
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(themeMode)
        }
    }
}
