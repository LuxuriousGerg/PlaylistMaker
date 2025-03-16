package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) : SettingsRepository {

    private val themeKey = "dark_theme"

    override fun isDarkThemeEnabled(): Boolean {
        val value = sharedPreferences.getBoolean(themeKey, false)
        return value
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(themeKey, enabled).apply()
    }
}
