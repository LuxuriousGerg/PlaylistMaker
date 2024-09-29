package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.net.Uri
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton: ImageView = findViewById(R.id.icon_back)
        backButton.setOnClickListener {
            finish() // Закрытие текущего экрана
        }

        // Инициализация кнопки "Поделиться"
        val shareButton: ImageView = findViewById(R.id.icon_share)
        shareButton.setOnClickListener {
            shareApp()
        }

        // Инициализация кнопки "Написать в поддержку"
        val supportButton: ImageView = findViewById(R.id.icon_support)
        supportButton.setOnClickListener {
            writeToSupport()
        }

        // Инициализация кнопки "Пользовательское соглашение"
        val userAgreementButton: ImageView = findViewById(R.id.icon_terms)
        userAgreementButton.setOnClickListener {
            openUserAgreement()
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




