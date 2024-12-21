package com.example.playlistmaker.Presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.interactors.SettingsInteractor
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Инициализация интерактора
        settingsInteractor = Creator.provideSettingsInteractor(this)

        // Инициализация Toolbar
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Установка заголовка
        supportActionBar?.title = getString(R.string.settings_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Обработка кнопки "Назад"
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Инициализация UI
        setupUI()

        // Настройка переключателя темы
        setupThemeSwitcher()
    }

    private fun setupUI() {
        // Обработка кнопки "Поделиться приложением"
        findViewById<MaterialTextView>(R.id.textView_share_app).setOnClickListener {
            shareApp()
        }

        // Обработка кнопки "Написать в поддержку"
        findViewById<MaterialTextView>(R.id.textView_support).setOnClickListener {
            writeToSupport()
        }

        // Обработка кнопки "Пользовательское соглашение"
        findViewById<MaterialTextView>(R.id.textView_terms).setOnClickListener {
            openUserAgreement()
        }

        // Инициализация переключателя темы
        themeSwitcher = findViewById(R.id.switch_theme)
    }

    private fun setupThemeSwitcher() {
        // Устанавливаем текущее состояние переключателя
        themeSwitcher.isChecked = settingsInteractor.isDarkThemeEnabled()

        // Обработка переключения темы
        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsInteractor.setDarkThemeEnabled(isChecked)
            applyTheme(isChecked)
        }
    }

    private fun applyTheme(isDarkThemeEnabled: Boolean) {
        val mode = if (isDarkThemeEnabled) {
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
        } else {
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
        }
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun shareApp() {
        val shareText = getString(R.string.share_message)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        if (shareIntent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(shareIntent, null))
        }
    }

    private fun writeToSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("support@example.com"))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email))
        }
        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(emailIntent)
        }
    }

    private fun openUserAgreement() {
        val url = getString(R.string.terms_url)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (browserIntent.resolveActivity(packageManager) != null) {
            startActivity(browserIntent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
