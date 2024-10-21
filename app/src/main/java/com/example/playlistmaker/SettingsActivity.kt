package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.net.Uri
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import android.content.SharedPreferences
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Инициализация и установка MaterialToolbar как ActionBar
        val toolbar: com.google.android.material.appbar.MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Установка заголовка
        supportActionBar?.title = getString(R.string.settings_title)

        // Обработка нажатия на кнопку "Назад"
        toolbar.setNavigationOnClickListener {
            finish() // Закрытие текущего экрана
        }

        // Обработка кнопки "Поделиться приложением"
        val shareTextView: MaterialTextView = findViewById(R.id.textView_share_app)
        shareTextView.setOnClickListener {
            shareApp()
        }

        // Обработка кнопки "Написать в поддержку"
        val supportTextView: MaterialTextView = findViewById(R.id.textView_support)
        supportTextView.setOnClickListener {
            writeToSupport()
        }

        // Обработка кнопки "Пользовательское соглашение"
        val termsTextView: MaterialTextView = findViewById(R.id.textView_terms)
        termsTextView.setOnClickListener {
            openUserAgreement()
        }

        // Инициализация переключателя темы
        val themeSwitcher: SwitchMaterial = findViewById(R.id.switch_theme)
        themeSwitcher.isChecked = (applicationContext as App).darkTheme

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
        }
    }


    // Метод для шаринга приложения
    private fun shareApp() {
        val shareText = getString(R.string.share_message) // Получаем текст для шаринга из strings.xml

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        if (shareIntent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(shareIntent, null))
        }
    }

    // Метод для отправки сообщения в поддержку
    private fun writeToSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("georggalileo21@yandex.ru"))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email))
        }

        if (emailIntent.resolveActivity(packageManager) != null) {
            startActivity(emailIntent)
        }
    }

    // Метод для открытия пользовательского соглашения
    private fun openUserAgreement() {
        val url = getString(R.string.terms_url) // Получаем URL из strings.xml

        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        if (browserIntent.resolveActivity(packageManager) != null) {
            startActivity(browserIntent)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                // Завершить текущую активность и вернуться назад
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
class App : Application() {

    var darkTheme = false
    private lateinit var preferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        darkTheme = preferences.getBoolean("dark_theme", false)

        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        preferences.edit().putBoolean("dark_theme", darkThemeEnabled).apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}




