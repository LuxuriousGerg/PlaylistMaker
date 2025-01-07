package com.example.playlistmaker.presentation.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.presentation.ui.library.LibraryActivity
import com.example.playlistmaker.presentation.ui.search.SearchActivity
import com.example.playlistmaker.presentation.ui.settings.SettingsActivity
import com.example.playlistmaker.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupNavigation()
    }

    private fun setupNavigation() {
        // Переход на экран поиска
        findViewById<Button>(R.id.button_search).setOnClickListener {
            navigateTo(SearchActivity::class.java)
        }

        // Переход на экран медиатеки
        findViewById<Button>(R.id.button_library).setOnClickListener {
            navigateTo(LibraryActivity::class.java)
        }

        // Переход на экран настроек
        findViewById<Button>(R.id.button_settings).setOnClickListener {
            navigateTo(SettingsActivity::class.java)
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}
