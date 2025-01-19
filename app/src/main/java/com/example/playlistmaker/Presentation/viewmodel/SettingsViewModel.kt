package com.example.playlistmaker.presentation.viewmodel

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.interactors.SettingsInteractor

class SettingsViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    private val _isDarkThemeEnabled = MutableLiveData<Boolean>()
    val isDarkThemeEnabled: LiveData<Boolean> get() = _isDarkThemeEnabled

    init {
        _isDarkThemeEnabled.value = settingsInteractor.isDarkThemeEnabled()
        Log.d("SettingsViewModel", "Загружена тема: ${_isDarkThemeEnabled.value}")
    }

    fun toggleTheme(isEnabled: Boolean) {
        Log.d("SettingsViewModel", "Изменение темы: $isEnabled")
        settingsInteractor.setDarkThemeEnabled(isEnabled)
        _isDarkThemeEnabled.value = isEnabled

        // 🔥 Применение темы сразу
        val mode = if (isEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }
}
