package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.*
import retrofit2.*
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
    private lateinit var progressBar: ProgressBar
    private lateinit var searchHistory: SearchHistory
    private lateinit var searchHistoryTitle: TextView

    private var queryText: String? = null
    private var searchJob: Job? = null
    private var clickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Инициализация Toolbar
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Инициализация компонентов
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        recyclerView = findViewById(R.id.recycler_view)
        retryButton = findViewById(R.id.retry_button)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progress_bar)
        searchHistoryTitle = findViewById(R.id.searchHistoryTitle)

        // Инициализация SharedPreferences и SearchHistory
        val sharedPreferences = getSharedPreferences("my_prefs", MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)

        // Настройка RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(arrayListOf())
        recyclerView.adapter = trackAdapter

        // Загрузить историю поиска при запуске
        loadSearchHistory()

        // Обработчик клика на элемент списка
        trackAdapter.setOnTrackClickListener { track ->
            if (clickAllowed) {
                clickAllowed = false
                Handler(Looper.getMainLooper()).postDelayed({ clickAllowed = true }, 1000)

                searchHistory.addTrack(track) // Сохранение трека в историю
                val intent = Intent(this, PlayerActivity::class.java).apply {
                    putExtra("track", track)
                }
                startActivity(intent)
            }
        }

        // Настройка кнопки "Очистить"
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            resetSearchUI()
        }

        // Кнопка очистки истории
        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            loadSearchHistory()
        }

        // Обработка ввода текста
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                queryText = s?.toString()
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                searchJob?.cancel()
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(2000)
                    queryText?.let {
                        if (it.isNotEmpty()) performSearch(it)
                    }
                }

                if (s.isNullOrEmpty()) {
                    resetSearchUI()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Кнопка "Повторить"
        retryButton.setOnClickListener {
            queryText?.let { performSearch(it) }
        }

        // Обработка нажатия на кнопку "Done" на клавиатуре
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                queryText?.let { performSearch(it) }
                hideKeyboard()
                true
            } else {
                false
            }
        }

        // Восстановление состояния при повороте экрана
        savedInstanceState?.let {
            queryText = it.getString("query_text")
            searchEditText.setText(queryText)
            if (!queryText.isNullOrEmpty()) {
                performSearch(queryText!!)
            }
        }
    }

    private fun performSearch(query: String) {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        hideError()

        // Скрываем заголовок истории и кнопку очистки истории только при реальном поиске
        findViewById<TextView>(R.id.searchHistoryTitle).visibility = View.GONE
        clearHistoryButton.visibility = View.GONE

        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(iTunesApiService::class.java)
        api.searchTracks(query).enqueue(object : Callback<Track.SearchResponse> {
            override fun onResponse(call: Call<Track.SearchResponse>, response: Response<Track.SearchResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val tracks = response.body()?.results ?: emptyList()
                    if (tracks.isEmpty()) {
                        showError("no_results")
                    } else {
                        trackAdapter.updateTracks(tracks)
                        recyclerView.visibility = View.VISIBLE
                        hideError()
                    }
                } else {
                    showError("connection")
                }
            }

            override fun onFailure(call: Call<Track.SearchResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                showError("connection")
            }
        })
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

    // Метод для скрытия клавиатуры
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    // Сохранение текста при изменении состояния
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("query_text", queryText)
    }

    // Интерфейс для iTunes API
    interface iTunesApiService {
        @GET("search")
        fun searchTracks(@Query("term") searchText: String): Call<Track.SearchResponse>
    }
}
