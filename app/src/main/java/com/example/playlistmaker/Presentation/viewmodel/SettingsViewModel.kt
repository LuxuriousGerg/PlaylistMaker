package com.example.playlistmaker.presentation.viewmodel

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.interactors.SettingsInteractor

class SettingsViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    private val _isDarkThemeEnabled = MutableLiveData<Boolean>()
    val isDarkThemeEnabled: LiveData<Boolean> get() = _isDarkThemeEnabled

    private val _shareAppEvent = MutableLiveData<Intent>()
    val shareAppEvent: LiveData<Intent> get() = _shareAppEvent

    private val _supportEmailEvent = MutableLiveData<Intent>()
    val supportEmailEvent: LiveData<Intent> get() = _supportEmailEvent

    private val _userAgreementEvent = MutableLiveData<Intent>()
    val userAgreementEvent: LiveData<Intent> get() = _userAgreementEvent

    init {
        _isDarkThemeEnabled.value = settingsInteractor.isDarkThemeEnabled()
    }

    fun toggleTheme(isEnabled: Boolean) {
        settingsInteractor.setDarkThemeEnabled(isEnabled)
        _isDarkThemeEnabled.value = isEnabled
    }

    fun shareApp(shareText: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        _shareAppEvent.value = shareIntent
    }

    fun writeToSupport(email: String, subject: String, message: String) {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }
        _supportEmailEvent.value = emailIntent
    }

    fun openUserAgreement(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        _userAgreementEvent.value = browserIntent
    }
}
