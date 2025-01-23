package com.example.playlistmaker.domain.interactors

import android.util.Log
import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {

    override fun isDarkThemeEnabled(): Boolean {
        val value = repository.isDarkThemeEnabled()
        Log.d("SettingsInteractor", "Чтение темы: $value")
        return value
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        Log.d("SettingsInteractor", "Сохранение темы: $enabled")
        repository.setDarkThemeEnabled(enabled)
    }
}
