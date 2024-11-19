package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class PlayerActivity : AppCompatActivity() {

    private lateinit var trackTitle: TextView
    private lateinit var artistName: TextView
    private lateinit var durationValue: TextView
    private lateinit var albumValue: TextView
    private lateinit var yearValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView
    private lateinit var playButton: ImageView
    private lateinit var pauseButton: ImageView
    private lateinit var currentTimeTextView: TextView

    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private val handler = Handler(Looper.getMainLooper())

    private val updateTimeRunnable = object : Runnable {
        private val timeFormatter = java.text.SimpleDateFormat("mm:ss", java.util.Locale.getDefault())

        override fun run() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    val currentPosition = it.currentPosition
                    currentTimeTextView.text = timeFormatter.format(currentPosition)
                    handler.postDelayed(this, 500)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player)

        // Инициализация UI-элементов
        trackTitle = findViewById(R.id.track_title)
        artistName = findViewById(R.id.artist_name)
        durationValue = findViewById(R.id.info_duration_value)
        albumValue = findViewById(R.id.info_album_value)
        yearValue = findViewById(R.id.info_year_value)
        genreValue = findViewById(R.id.info_genre_value)
        countryValue = findViewById(R.id.info_country_value)
        playButton = findViewById(R.id.play) // кнопка "Играть"
        pauseButton = findViewById(R.id.pause) // кнопка "Пауза"
        currentTimeTextView = findViewById(R.id.current_time) // текст для текущего времени

        // Включение прокрутки (marquee) для trackTitle и artistName
        trackTitle.isSelected = true
        artistName.isSelected = true
        albumValue.isSelected = true // Если требуется прокрутка для альбома

        val track = intent.getSerializableExtra("track") as? Track

        // Установка информации о треке
        track?.let {
            trackTitle.text = it.trackName
            artistName.text = it.artistName
            durationValue.text = track.formatTrackTime(it.trackTimeMillis)
            albumValue.text = it.collectionName
            yearValue.text = it.getReleaseYear()
            genreValue.text = it.primaryGenreName
            countryValue.text = it.country

            Glide.with(this)
                .load(it.getCoverArtwork())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error)
                .into(findViewById(R.id.album_cover))

            // Настройка MediaPlayer
            setupMediaPlayer(it.previewUrl)
        }

        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            stopPlaybackAndFinish()
        }

        // Обработка нажатия на кнопки "Играть" и "Пауза"
        playButton.setOnClickListener {
            togglePlayback()
        }
        pauseButton.setOnClickListener {
            togglePlayback()
        }
    }

    private fun stopPlaybackAndFinish() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
        finish()
    }

    private fun setupMediaPlayer(url: String?) {
        if (url != null) {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    // Готовность к воспроизведению, кнопка "Пауза" скрыта, показываем "Играть"
                    playButton.visibility = View.VISIBLE
                    pauseButton.visibility = View.GONE
                }
                setOnCompletionListener {
                    // Обработка завершения воспроизведения
                    playButton.visibility = View.VISIBLE
                    pauseButton.visibility = View.GONE
                    currentTimeTextView.text = "00:00"
                    handler.removeCallbacks(updateTimeRunnable)
                }
            }
        }
    }

    private fun togglePlayback() {
        mediaPlayer?.let {
            if (isPlaying) {
                // Приостановка воспроизведения
                it.pause()
                playButton.visibility = View.VISIBLE
                pauseButton.visibility = View.GONE
                handler.removeCallbacks(updateTimeRunnable)
            } else {
                // Начало или продолжение воспроизведения
                it.start()
                playButton.visibility = View.GONE
                pauseButton.visibility = View.VISIBLE
                handler.post(updateTimeRunnable)
            }
            isPlaying = !isPlaying
        }
    }

    override fun onPause() {
        super.onPause()
        // Остановка воспроизведения при выходе из активности
        if (isPlaying) {
            togglePlayback()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacks(updateTimeRunnable)
    }
}
