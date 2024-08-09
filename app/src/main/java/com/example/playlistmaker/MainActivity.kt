package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Кнопка для перехода на экран поиска
        val searchButton = findViewById<Button>(R.id.button_search)
        searchButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // Кнопка для перехода на экран медиатеки
        val libraryButton = findViewById<Button>(R.id.button_library)
        libraryButton.setOnClickListener {
            val intent = Intent(this, LibraryActivity::class.java)
            startActivity(intent)
        }

        // Кнопка для перехода на экран настроек
        val settingsButton = findViewById<Button>(R.id.button_settings)
        settingsButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}