package com.example.playlistmaker.presentation.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.interactors.SettingsInteractor
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial
    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Инициализируем интерактор через DI
        settingsInteractor = Creator.provideSettingsInteractor(this)

        // Устанавливаем Toolbar
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Установка заголовка
        supportActionBar?.title = getString(R.string.settings_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Инициализируем UI
        setupUI()
        setupThemeSwitcher()
    }

    private fun setupUI() {
        // Кнопка "Поделиться приложением"
        findViewById<MaterialTextView>(R.id.textView_share_app).setOnClickListener {
            shareApp()
        }

        // Кнопка "Написать в поддержку"
        findViewById<MaterialTextView>(R.id.textView_support).setOnClickListener {
            writeToSupport()
        }

        // Кнопка "Пользовательское соглашение"
        findViewById<MaterialTextView>(R.id.textView_terms).setOnClickListener {
            openUserAgreement()
        }

        // Инициализация переключателя темы
        themeSwitcher = findViewById(R.id.switch_theme)
    }

    private fun setupThemeSwitcher() {
        // Устанавливаем текущее состояние переключателя
        themeSwitcher.isChecked = settingsInteractor.isDarkThemeEnabled()

        // Слушатель переключателя темы
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
        startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun writeToSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("support@example.com"))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email))
        }
        startActivity(Intent.createChooser(emailIntent, getString(R.string.contact_support)))
    }

    private fun openUserAgreement() {
        val url = getString(R.string.terms_url)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
