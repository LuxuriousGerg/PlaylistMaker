package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView

class SearchActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView

    // Переменная для сохранения текста
    private var queryText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton: ImageView = findViewById(R.id.icon_back)
        searchView = findViewById(R.id.search_view)
        searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        clearButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn)

        // Инициализация подсказки (hint)
        searchEditText.hint = getString(R.string.search_hint)
        searchEditText.maxLines = 1
        searchEditText.isSingleLine = true

        searchView.setOnClickListener {
            searchView.isIconified = false
        }

        // Кнопка "Назад"
        backButton.setOnClickListener {
            finish()
        }

        // Скрываем кнопку "Очистить запрос" по умолчанию
        clearButton.visibility = View.GONE

        // Восстанавливаем текст, если он есть
        queryText?.let {
            searchEditText.setText(it)
        }

        // Обработка ввода текста
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Сохраняем текущий текст в переменную queryText
                queryText = s?.toString()

                // Показать кнопку "Очистить запрос", если есть текст
                if (s.isNullOrEmpty()) {
                    clearButton.visibility = View.GONE
                } else {
                    clearButton.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // Обработка нажатия на кнопку "Очистить запрос"
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard()
            clearButton.visibility = View.GONE
        }

        // Обработка отправки текста при поиске
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Логика поиска при нажатии на "Поиск"
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Логика обновления при изменении текста
                return false
            }
        })
    }

    // Сохраняем состояние активности
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохраняем текст в Bundle только если он не пуст
        if (!queryText.isNullOrEmpty()) {
            outState.putString("QUERY_TEXT", queryText)
        }
    }

    // Восстанавливаем состояние активности
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Восстанавливаем текст из Bundle
        queryText = savedInstanceState.getString("QUERY_TEXT")
        queryText?.let {
            searchEditText.setText(it)
        }
    }

    // Метод для скрытия клавиатуры
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }
}

