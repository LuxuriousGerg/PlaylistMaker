package com.example.playlistmaker.presentation.ui.player

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.viewmodel.PlayerViewModel

class PlayerActivity : AppCompatActivity() {
    private lateinit var trackTitle: TextView
    private lateinit var artistName: TextView
    private lateinit var playButton: ImageView
    private lateinit var pauseButton: ImageView
    private lateinit var backButton: ImageButton
    private lateinit var currentTimeTextView: TextView

    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player)

        setupUI()
        setupObservers()

        val track = intent.getSerializableExtra("track") as? Track
        track?.let { playerViewModel.preparePlayer(it) }
    }

    private fun setupUI() {
        trackTitle = findViewById(R.id.track_title)
        artistName = findViewById(R.id.artist_name)
        playButton = findViewById(R.id.play)
        pauseButton = findViewById(R.id.pause)
        backButton = findViewById(R.id.back_button)
        currentTimeTextView = findViewById(R.id.current_time)

        backButton.setOnClickListener { finish() }
        playButton.setOnClickListener { playerViewModel.togglePlayback() }
        pauseButton.setOnClickListener { playerViewModel.togglePlayback() }
    }

    private fun setupObservers() {
        playerViewModel.track.observe(this) { track ->
            trackTitle.text = track.trackName
            artistName.text = track.artistName

            Glide.with(this)
                .load(track.getCoverArtwork())
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
