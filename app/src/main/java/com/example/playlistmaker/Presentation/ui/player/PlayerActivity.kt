package com.example.playlistmaker.presentation.ui.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.interactors.PlayerInteractor
import com.example.playlistmaker.domain.models.Track

class PlayerActivity : AppCompatActivity() {
    private lateinit var trackTitle: TextView
    private lateinit var artistName: TextView
    private lateinit var playButton: ImageView
    private lateinit var pauseButton: ImageView
    private lateinit var currentTimeTextView: TextView
    private lateinit var backButton: ImageButton
    private lateinit var durationValue: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView


    private val handler = Handler(Looper.getMainLooper())
    private lateinit var playerInteractor: PlayerInteractor
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player)
        // Инициализация кнопки "Назад"
        backButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish() // Закрытие текущей активности
        }
        // Получаем интерактор
        playerInteractor = Creator.providePlayerInteractor()

        // Инициализация UI
        setupUI()

        // Включение прокрутки (marquee) для trackTitle и artistName
        trackTitle.isSelected = true
        artistName.isSelected = true
        albumValue.isSelected = true // Если требуется прокрутка для альбома

        // Получаем переданный трек
        val track = intent.getSerializableExtra("track") as? Track
        track?.let { setupTrackInfo(it) }
    }

    private fun setupUI() {
        trackTitle = findViewById(R.id.track_title)
        artistName = findViewById(R.id.artist_name)
        playButton = findViewById(R.id.play)
        pauseButton = findViewById(R.id.pause)
        currentTimeTextView = findViewById(R.id.current_time)
        durationValue = findViewById(R.id.info_duration_value)
        albumValue = findViewById(R.id.info_album_value)
        yearValue = findViewById(R.id.info_year_value)
        genreValue = findViewById(R.id.info_genre_value)
        countryValue = findViewById(R.id.info_country_value)

        playButton.setOnClickListener { togglePlayback() }
        pauseButton.setOnClickListener { togglePlayback() }
    }

    private fun setupTrackInfo(track: Track) {
        trackTitle.text = track.trackName
        artistName.text = track.artistName
        durationValue.text = track.formatTrackTime(track.trackTimeMillis)
        albumValue.text = track.collectionName
        yearValue.text = track.getReleaseYear()
        genreValue.text = track.primaryGenreName
        countryValue.text = track.country

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder_image)
            .into(findViewById(R.id.album_cover))

        playerInteractor.preparePlayer(
            track.previewUrl ?: "",
            onPrepared = {
                playButton.visibility = View.VISIBLE
                pauseButton.visibility = View.GONE
            },
            onCompletion = {
                playButton.visibility = View.VISIBLE
                pauseButton.visibility = View.GONE
                currentTimeTextView.text = "00:00"
                handler.removeCallbacks(updateTimeRunnable)
            }
        )
    }

    private fun togglePlayback() {
        if (isPlaying) {
            playerInteractor.pause()
            playButton.visibility = View.VISIBLE
            pauseButton.visibility = View.GONE
        } else {
            playerInteractor.play()
            playButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
            handler.post(updateTimeRunnable)
        }
        isPlaying = !isPlaying
    }

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (playerInteractor.isPlaying()) {
                currentTimeTextView.text = formatTime(playerInteractor.getCurrentPosition())
                handler.postDelayed(this, 500)
            }
        }
    }

    private fun formatTime(milliseconds: Int): String {
        val seconds = milliseconds / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        playerInteractor.release()
        handler.removeCallbacks(updateTimeRunnable)
    }
}
