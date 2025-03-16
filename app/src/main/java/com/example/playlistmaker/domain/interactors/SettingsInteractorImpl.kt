package com.example.playlistmaker.domain.interactors

import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {

    override fun isDarkThemeEnabled(): Boolean {
        val value = repository.isDarkThemeEnabled()
        return value
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        repository.setDarkThemeEnabled(enabled)
    }
}
