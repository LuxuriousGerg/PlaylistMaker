package com.example.playlistmaker.presentation.ui.player

import PlayerViewModel
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.viewmodel.FavoritesViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

// Функция-расширение для установки обработчика кликов с debounce
fun View.setDebouncedOnClickListener(
    delayMs: Long = 300L,
    coroutineScope: CoroutineScope,
    action: () -> Unit
) {
    var debounceJob: Job? = null
    setOnClickListener {
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            delay(delayMs)
            action()
        }
    }
}

class PlayerActivity : AppCompatActivity() {
    private lateinit var trackTitle: TextView
    private lateinit var artistName: TextView
    private lateinit var playButton: ImageView
    private lateinit var pauseButton: ImageView
    private lateinit var backButton: ImageButton
    private lateinit var currentTimeTextView: TextView

    // Кнопки для состояния "Нравится"
    private lateinit var likeButton: ImageView
    private lateinit var likePressedButton: ImageView
    // Храним текущий трек
    private lateinit var currentTrack: Track

    private val playerViewModel: PlayerViewModel by viewModel()
    // Внедряем FavoritesViewModel для работы с избранным
    private val favoritesViewModel: FavoritesViewModel by viewModel()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player)

        setupUI()
        setupObservers()

        // Инициализируем кнопки "Нравится"
        likeButton = findViewById(R.id.like)
        likePressedButton = findViewById(R.id.like_pressed)

        // Получаем трек из intent
        val track = intent.getParcelableExtra("track", Track::class.java)
        track?.let {
            currentTrack = it
            trackTitle.text = it.trackName
            artistName.text = it.artistName
            it.previewUrl?.let { url ->
                playerViewModel.preparePlayer(url)
            }

            Glide.with(this)
                .load(it.getCoverArtwork())
                .placeholder(R.drawable.placeholder_image)
                .into(findViewById(R.id.album_cover))

            findViewById<TextView>(R.id.info_album_value).text = it.collectionName ?: "Unknown Album"
            findViewById<TextView>(R.id.info_year_value).text = it.getReleaseYear()
            findViewById<TextView>(R.id.info_genre_value).text = it.primaryGenreName ?: "Unknown Genre"
            findViewById<TextView>(R.id.info_country_value).text = it.country ?: "Unknown Country"
            findViewById<TextView>(R.id.info_duration_value).text = it.formatTrackTime(it.trackTimeMillis)

            // Устанавливаем начальное состояние кнопки "Нравится"
            updateFavoriteButton()
        }

        // Назначаем обработчики кликов для обеих кнопок (выбранное и не выбранное состояние)
        likeButton.setOnClickListener { toggleFavorite() }
        likePressedButton.setOnClickListener { toggleFavorite() }
    }

    // Обновляет видимость кнопок в зависимости от флага isFavorite
    private fun updateFavoriteButton() {
        if (currentTrack.isFavorite) {
            likeButton.visibility = View.GONE
            likePressedButton.visibility = View.VISIBLE
        } else {
            likeButton.visibility = View.VISIBLE
            likePressedButton.visibility = View.GONE
        }
    }

    // Переключает состояние избранного для текущего трека
    private fun toggleFavorite() {
        favoritesViewModel.toggleFavorite(currentTrack)
        // Переключаем локальное состояние и обновляем UI
        currentTrack.isFavorite = !currentTrack.isFavorite
        updateFavoriteButton()
    }

    private fun setupUI() {
        trackTitle = findViewById(R.id.track_title)
        artistName = findViewById(R.id.artist_name)
        playButton = findViewById(R.id.play)
        pauseButton = findViewById(R.id.pause)
        backButton = findViewById(R.id.back_button)
        currentTimeTextView = findViewById(R.id.current_time)

        trackTitle.isSelected = true
        artistName.isSelected = true
        findViewById<TextView>(R.id.info_album_value).isSelected = true

        backButton.setOnClickListener {
            finish()
        }
        // Используем debounced обработчик кликов для переключения воспроизведения
        playButton.setDebouncedOnClickListener(delayMs = 300L, coroutineScope = lifecycleScope) {
            playerViewModel.togglePlayback()
        }
        pauseButton.setDebouncedOnClickListener(delayMs = 300L, coroutineScope = lifecycleScope) {
            playerViewModel.togglePlayback()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setupObservers() {
        val track: Track? = intent.getParcelableExtra("track", Track::class.java)

        track?.let {
            trackTitle.text = it.trackName
            artistName.text = it.artistName
            Glide.with(this)
                .load(it.getCoverArtwork())
                .placeholder(R.drawable.placeholder_image)
                .into(findViewById(R.id.album_cover))
        }

        playerViewModel.isPlaying.observe(this) { isPlaying ->
            playButton.visibility = if (isPlaying) View.GONE else View.VISIBLE
            pauseButton.visibility = if (isPlaying) View.VISIBLE else View.GONE
        }

        playerViewModel.currentTime.observe(this) { time ->
            currentTimeTextView.text = time
        }
    }
}
