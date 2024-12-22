package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) : SettingsRepository {

    private val themeKey = "dark_theme"

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(themeKey, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(themeKey, enabled).apply()
    }
}
