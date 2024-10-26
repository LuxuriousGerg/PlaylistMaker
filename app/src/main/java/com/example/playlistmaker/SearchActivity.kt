package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var retryButton: Button
    private lateinit var clearHistoryButton: Button
    private lateinit var searchHistory: SearchHistory

    private var queryText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Инициализация Toolbar
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.search_hint)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Инициализация компонентов
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        recyclerView = findViewById(R.id.recycler_view)
        retryButton = findViewById(R.id.retry_button)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        // Инициализация SharedPreferences и SearchHistory
        val sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        // Инициализация RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(arrayListOf())
        recyclerView.adapter = trackAdapter

        // Привязка адаптера к RecyclerView с пустым списком
        trackAdapter = TrackAdapter(arrayListOf())
        recyclerView.adapter = trackAdapter

// Устанавливаем слушатель кликов
        trackAdapter.setOnTrackClickListener { track ->
            // Добавляем трек в историю поиска
            searchHistory.addTrack(track)

            // Переход на экран «Аудиоплеера»
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("track", track) // Передаем трек
            }
            startActivity(intent)
        }
        recyclerView.adapter = trackAdapter

        // Скрываем кнопку "Очистить" по умолчанию
        clearButton.visibility = View.GONE

        // Загружаем историю поиска при старте
        loadSearchHistory()

        // Обработка ввода текста
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                queryText = s?.toString()

                // Показать кнопку "Очистить", если текст не пуст
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                // Если текст очищается, сбрасываем UI
                if (s.isNullOrEmpty()) {
                    resetSearchUI() // Сброс интерфейса
                } else {
                    // Скрываем заголовок истории и кнопку очистки, когда пользователь что-то вводит
                    val searchHistoryTitle: TextView = findViewById(R.id.searchHistoryTitle)
                    searchHistoryTitle.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Обработка нажатия на кнопку "Done" на клавиатуре
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                queryText?.let { performSearch(it) } // Проверка на пустой текст
                hideKeyboard()
                true
            } else {
                false
            }
        }

        // Обработка нажатия на кнопку "Очистить"
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            resetSearchUI()
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory() // Очищаем историю
            loadSearchHistory()
        }

        setupRetryButton()

        // Восстановление состояния из savedInstanceState
        savedInstanceState?.let {
            queryText = it.getString("query_text")
            searchEditText.setText(queryText)
            if (!queryText.isNullOrEmpty()) {
                performSearch(queryText!!)
            }
        }
    }

    // Выполнение поиска по запросу
    private fun performSearch(query: String) {
        if (query.isNotEmpty()) {
            Log.d("SearchActivity", "Выполняется поиск для: $query")

            // Локальная инициализация Retrofit
            val retrofit = Retrofit.Builder()
                .baseUrl("https://itunes.apple.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(iTunesApiService::class.java)
            api.searchTracks(query).enqueue(object : Callback<SearchResponse> {
                override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                    Log.d("SearchActivity", "Ответ от API: ${response.code()}")

                    if (response.isSuccessful) {
                        val searchResponse = response.body()
                        val tracks = searchResponse?.results ?: emptyList()

                        Log.d("SearchActivity", "Найдено треков: ${tracks.size}")

                        if (tracks.isEmpty()) {
                            showError("no_results") // Показываем ошибку, если нет результатов
                        } else {
                            trackAdapter.updateTracks(tracks) // Обновляем адаптер
                            recyclerView.visibility = View.VISIBLE // Показываем RecyclerView при наличии данных
                            hideError() // Скрываем ошибку, если есть данные

                            retryButton.visibility = View.GONE // Скрываем кнопку "Повторить" после успешного запроса
                        }
                    } else {
                        Log.e("SearchActivity", "Ошибка запроса: ${response.code()}")
                        showError("connection")
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    Log.e("SearchActivity", "Ошибка сети: ${t.message}")
                    showError("connection")
                }
            })
        }
    }

    // Метод для загрузки истории поиска
    private fun loadSearchHistory() {
        val history = searchHistory.getHistory()
        trackAdapter.updateTracks(history)

        // Показать/скрыть элементы UI, такие как заголовок и кнопка очистки
        val searchHistoryTitle: TextView = findViewById(R.id.searchHistoryTitle)
        searchHistoryTitle.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
        clearHistoryButton.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
    }

    // Метод для отображения ошибок
    private fun showError(errorType: String) {
        val errorIcon: ImageView = findViewById(R.id.error_icon)
        val errorText: TextView = findViewById(R.id.error_text)
        val errorIcon2: ImageView = findViewById(R.id.error_icon2)
        val errorText2: TextView = findViewById(R.id.error_text2)

        when (errorType) {
            "connection" -> {
                errorIcon.setImageResource(R.drawable.error2)
                errorText.text = getString(R.string.connection_error_text)
                errorIcon.visibility = View.VISIBLE
                errorText.visibility = View.VISIBLE
                retryButton.visibility = View.VISIBLE
                errorIcon2.visibility = View.GONE
                errorText2.visibility = View.GONE
            }
            "no_results" -> {
                errorIcon2.setImageResource(R.drawable.error)
                errorText2.text = getString(R.string.no_results_text)
                errorIcon2.visibility = View.VISIBLE
                errorText2.visibility = View.VISIBLE
                retryButton.visibility = View.GONE
                errorIcon.visibility = View.GONE
                errorText.visibility = View.GONE
            }
        }

        recyclerView.visibility = View.GONE
    }

    private fun hideError() {
        findViewById<ImageView>(R.id.error_icon).visibility = View.GONE
        findViewById<TextView>(R.id.error_text).visibility = View.GONE
        findViewById<ImageView>(R.id.error_icon2).visibility = View.GONE
        findViewById<TextView>(R.id.error_text2).visibility = View.GONE
    }

    // Обработка нажатия на кнопку "Повторить" для перезапуска поиска
    private fun setupRetryButton() {
        retryButton.setOnClickListener {
            queryText?.let { performSearch(it) }
        }
    }

    // Метод для скрытия клавиатуры
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    // Сброс интерфейса при очистке запроса
    private fun resetSearchUI() {
        searchEditText.clearFocus()

        // Скрыть все placeholder'ы
        findViewById<View>(R.id.error_icon)?.visibility = View.GONE
        findViewById<View>(R.id.error_text)?.visibility = View.GONE
        findViewById<View>(R.id.retry_button)?.visibility = View.GONE
        findViewById<View>(R.id.error_icon2)?.visibility = View.GONE
        findViewById<View>(R.id.error_text2)?.visibility = View.GONE

        // Загрузить историю поиска, если поле поиска пустое
        loadSearchHistory()

        // Показываем RecyclerView только если есть история поиска
        recyclerView.visibility = if (searchHistory.getHistory().isNotEmpty()) View.VISIBLE else View.GONE
    }

    // Сохранение текста при изменении состояния
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("query_text", queryText)
    }

    // Интерфейс для iTunes API
    interface iTunesApiService {
        @GET("search")
        fun searchTracks(@Query("term") searchText: String): Call<SearchResponse>
    }
}




