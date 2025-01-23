package com.example.playlistmaker.presentation.ui.search

import SearchViewModel
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.player.PlayerActivity
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

    private fun setupListeners() {
        retryButton.setOnClickListener {
            searchViewModel.viewModelScope.launch {
                startProgressBarAnimation()
                searchViewModel.searchTracks(query = searchEditText.text.toString().trim())
            }
        }

        trackAdapter.setOnTrackClickListener { track ->
            searchViewModel.addToSearchHistory(track)
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }
        clearHistoryButton.setOnClickListener {
            searchViewModel.clearHistory()
            clearHistoryButton.visibility = View.GONE
        }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            searchViewModel.loadSearchHistory()
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
                    startProgressBarAnimation()

                    searchViewModel.viewModelScope.launch {
                        kotlinx.coroutines.delay(2000)
                        searchViewModel.searchTracks(query = s.toString().trim())
                    }
                } else {
                    searchViewModel.loadSearchHistory()
                    hideError()
                }
            }


            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupObservers() {
        searchViewModel.searchResults.observe(this) { tracks ->
            trackAdapter.updateTracks(tracks)
            recyclerView.visibility = if (tracks.isNotEmpty()) View.VISIBLE else View.GONE
            stopProgressBarAnimation()

            // Если ошибка уже установлена, не сбрасываем ее
            if (tracks.isEmpty() && searchViewModel.errorType.value == null) {
                showError("no_results")
            } else {
                hideError()
            }
        }

        searchViewModel.errorType.observe(this) { error ->
            error?.let { showError(it) } ?: hideError()
        }

        searchViewModel.searchHistory.observe(this) { history ->
            trackAdapter.updateTracks(history)
            clearHistoryButton.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
            searchHistoryTitle.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
            recyclerView.visibility = if (history.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun stopProgressBarAnimation() {
        progressBar.visibility = View.GONE
    }

    private fun startProgressBarAnimation() {
        progressBar.visibility = View.VISIBLE // Показываем прогресс-бар перед анимацией
        val animator = ObjectAnimator.ofFloat(progressBar, "rotation", 0f, 360f)
        animator.duration = 1000
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.start()
    }

    private fun showError(errorType: String) {
        when (errorType) {
            "connection" -> {
                errorIcon.setImageResource(R.drawable.error2)
                errorText.text = getString(R.string.connection_error_text)
                errorIcon.visibility = View.VISIBLE
                errorText.visibility = View.VISIBLE
                retryButton.visibility = View.VISIBLE
                errorIconNoResults.visibility = View.GONE
                errorTextNoResults.visibility = View.GONE
            }
            "no_results" -> {
                errorIconNoResults.setImageResource(R.drawable.error)
                errorTextNoResults.text = getString(R.string.no_results_text)
                errorIconNoResults.visibility = View.VISIBLE
                errorTextNoResults.visibility = View.VISIBLE
                retryButton.visibility = View.GONE
                errorIcon.visibility = View.GONE
                errorText.visibility = View.GONE
            }
        }
        recyclerView.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }

    private fun hideError() {
        errorIcon.visibility = View.GONE
        errorText.visibility = View.GONE
        errorIconNoResults.visibility = View.GONE
        errorTextNoResults.visibility = View.GONE
        retryButton.visibility = View.GONE
    }
}
