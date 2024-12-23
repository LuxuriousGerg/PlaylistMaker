package com.example.playlistmaker.presentation.ui.search

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.data.repository.SearchHistory
import com.example.playlistmaker.domain.interactors.HistoryInteractor
import com.example.playlistmaker.domain.interactors.SearchTracksInteractor
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var retryButton: Button
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var searchHistoryTitle: TextView
    private lateinit var historyInteractor: HistoryInteractor
    private lateinit var searchInteractor: SearchTracksInteractor
    private lateinit var searchHistory: SearchHistory

    private var queryText: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        searchHistory = SearchHistory(getSharedPreferences("my_prefs", MODE_PRIVATE))
        // Инициализация интеракторов
        historyInteractor = Creator.provideHistoryInteractor(this)
        searchInteractor = Creator.provideSearchTracksInteractor()

        // Инициализация Toolbar
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.search_hint)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Инициализация UI
        setupUI()

        // Загрузка истории поиска
        loadSearchHistory()

        // Настройка слушателей
        setupListeners()
    }

    private fun setupUI() {
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        recyclerView = findViewById(R.id.recycler_view)
        retryButton = findViewById(R.id.retry_button)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progress_bar)
        searchHistoryTitle = findViewById(R.id.searchHistoryTitle)

        recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(arrayListOf())
        recyclerView.adapter = trackAdapter
    }

    private fun setupListeners() {
        // Очистка поля ввода
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            resetSearchUI()
        }

        // Очистка истории
        clearHistoryButton.setOnClickListener {
            historyInteractor.clearHistory()
            loadSearchHistory()
        }

        // Обработка ввода текста
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                queryText = s?.toString()
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                searchRunnable?.let { handler.removeCallbacks(it) }

                if (queryText.isNullOrEmpty()) {
                    resetSearchUI()
                } else {
                    searchRunnable = Runnable { performSearch(queryText!!) }
                    handler.postDelayed(searchRunnable!!, 2000)
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Обработка нажатия "Done" на клавиатуре
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Отменяем запланированный Runnable перед выполнением поиска
                searchRunnable?.let { handler.removeCallbacks(it) }
                if (queryText?.isNotEmpty() == true) {
                    performSearch(queryText!!)
                }
                hideKeyboard()
                true
            } else false
        }

        // Обработчик кликов на элементы списка
        trackAdapter.setOnTrackClickListener { track ->
            historyInteractor.addTrackToHistory(track)
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("track", track)
            }
            startActivity(intent)
        }
    }

    private fun loadSearchHistory() {
        val history = historyInteractor.getHistory()
        trackAdapter.updateTracks(history)

        searchHistoryTitle.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
        clearHistoryButton.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun startProgressBarAnimation() {
        val animator = ObjectAnimator.ofFloat(progressBar, "rotation", 0f, 360f)
        animator.duration = 1000 // Продолжительность анимации
        animator.repeatCount = ObjectAnimator.INFINITE // Бесконечное повторение
        animator.start()
    }

    private fun performSearch(query: String) {
        Log.d("SearchActivity", "Performing search for query: $query")
        progressBar.visibility = View.VISIBLE
        startProgressBarAnimation()
        recyclerView.visibility = View.GONE
        searchHistoryTitle.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val tracks = searchInteractor.search(query).filter { it.trackTimeMillis > 0 }
                progressBar.visibility = View.GONE
                if (tracks.isEmpty()) {
                    showError("no_results")
                } else {
                    trackAdapter.updateTracks(tracks)
                    recyclerView.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                showError("connection")
            }
        }
    }

    private fun resetSearchUI() {
        searchEditText.clearFocus()
        loadSearchHistory()

        findViewById<View>(R.id.error_icon)?.visibility = View.GONE
        findViewById<View>(R.id.error_text)?.visibility = View.GONE
        findViewById<View>(R.id.retry_button)?.visibility = View.GONE
        findViewById<View>(R.id.error_icon2)?.visibility = View.GONE
        findViewById<View>(R.id.error_text2)?.visibility = View.GONE

        if (::searchHistory.isInitialized) {
            val history = searchHistory.getHistory()
            trackAdapter.updateTracks(history)
            searchHistoryTitle.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
            clearHistoryButton.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
            recyclerView.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
        } else {
            Log.e("SearchActivity", "searchHistory не инициализирован")
        }
    }

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

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }
}
