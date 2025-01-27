package com.example.playlistmaker.presentation.ui.search

import SearchViewModel
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchHistoryTitle: TextView
    private lateinit var retryButton: Button
    private lateinit var clearHistoryButton: Button
    private lateinit var errorIcon: ImageView
    private lateinit var errorText: TextView
    private lateinit var errorIconNoResults: ImageView
    private lateinit var errorTextNoResults: TextView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var toolbar: Toolbar
    private val searchViewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        setupUI()
        setupObservers()
        setupListeners()

        searchViewModel.loadSearchHistory()
    }

    private fun setupUI() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.search)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        recyclerView = findViewById(R.id.recycler_view)
        progressBar = findViewById(R.id.progress_bar)
        searchHistoryTitle = findViewById(R.id.searchHistoryTitle)
        retryButton = findViewById(R.id.retry_button)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        errorIcon = findViewById(R.id.error_icon)
        errorText = findViewById(R.id.error_text)
        errorIconNoResults = findViewById(R.id.error_icon2)
        errorTextNoResults = findViewById(R.id.error_text2)

        recyclerView.layoutManager = LinearLayoutManager(this)
        trackAdapter = TrackAdapter(arrayListOf())
        recyclerView.adapter = trackAdapter
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            combine(
                searchViewModel.searchResults,
                searchViewModel.errorType,
                searchViewModel.searchHistory
            ) { tracks, error, history ->
                Triple(tracks, error, history)
            }.collect { (tracks, error, history) ->
                stopProgressBarAnimation() // Останавливаем анимацию загрузки

                if (searchEditText.text.isEmpty()) {
                    // Сначала проверяем, что поле пустое — показываем историю
                    trackAdapter.updateTracks(history)
                    searchHistoryTitle.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
                    clearHistoryButton.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
                    recyclerView.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
                    hideError()
                } else if (tracks.isNotEmpty()) {
                    // Тогда только если поле не пустое, показываем результаты поиска
                    trackAdapter.updateTracks(tracks)
                    recyclerView.visibility = View.VISIBLE
                    hideError()
                } else if (error == "no_results") {
                    recyclerView.visibility = View.GONE
                    showError("no_results")
                } else if (error != null) {
                    showError(error)
                }
            }
        }
    }


    private fun setupListeners() {
        retryButton.setOnClickListener {
            lifecycleScope.launch {
                startProgressBarAnimation()
                searchViewModel.updateQuery(searchEditText.text.toString().trim())
            }
        }

        trackAdapter.setOnTrackClickListener { track ->
            searchViewModel.addToSearchHistory(track)
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra("track", track)
            }
            startActivity(intent)
        }


        clearHistoryButton.setOnClickListener {
            searchViewModel.clearHistory()
            searchHistoryTitle.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
            recyclerView.visibility = View.GONE
        }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            searchViewModel.updateQuery("")
            hideError()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE

                if (!s.isNullOrEmpty()) {
                    searchHistoryTitle.visibility = View.GONE
                    clearHistoryButton.visibility = View.GONE
                    recyclerView.visibility = View.GONE

                    searchViewModel.updateQuery(s.toString().trim()) // Просто обновляем query

                } else {
                    searchViewModel.loadSearchHistory()
                    hideError()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

        private fun stopProgressBarAnimation() {
        progressBar.visibility = View.GONE
    }

    private fun startProgressBarAnimation() {
        progressBar.visibility = View.VISIBLE
        val animator = ObjectAnimator.ofFloat(progressBar, "rotation", 0f, 360f)
        animator.duration = 1000
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.start()
    }

    private fun hideHistory() {
        searchHistoryTitle.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        recyclerView.visibility = View.GONE
    }

    private fun showError(errorType: String) {
        when (errorType) {
            "connection" -> {
                errorIcon.setImageResource(R.drawable.error2)
                errorText.text = getString(R.string.connection_error_text)
                errorIcon.visibility = View.VISIBLE
                errorText.visibility = View.VISIBLE
                retryButton.visibility = View.VISIBLE

                // Скрываем ненужные элементы
                errorIconNoResults.visibility = View.GONE
                errorTextNoResults.visibility = View.GONE
                recyclerView.visibility = View.GONE
                searchHistoryTitle.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
            }
            "no_results" -> {
                errorIconNoResults.setImageResource(R.drawable.error)
                errorTextNoResults.text = getString(R.string.no_results_text)
                errorIconNoResults.visibility = View.VISIBLE
                errorTextNoResults.visibility = View.VISIBLE

                // Показываем ошибку, но скрываем другие элементы
                retryButton.visibility = View.GONE
                recyclerView.visibility = View.GONE
                searchHistoryTitle.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
                errorIcon.visibility = View.GONE
                errorText.visibility = View.GONE
            }
        }
    }


    private fun hideError() {
        errorIcon.visibility = View.GONE
        errorText.visibility = View.GONE
        errorIconNoResults.visibility = View.GONE
        errorTextNoResults.visibility = View.GONE
        retryButton.visibility = View.GONE
    }
}
