package com.example.playlistmaker.presentation.ui.player

import PlayerViewModel
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.presentation.ui.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

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

    private lateinit var likeButton: ImageView
    private lateinit var likePressedButton: ImageView
    private lateinit var currentTrack: Track

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetAdapter: BottomSheetPlaylistAdapter

    private val playerViewModel: PlayerViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player)

        setupUI()
        setupObservers()

        likeButton = findViewById(R.id.like)
        likePressedButton = findViewById(R.id.like_pressed)

        val overlay = findViewById<View>(R.id.overlay)
        val bottomSheetContainer = findViewById<LinearLayout>(R.id.playlists_bottom_sheet)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            val screenHeight = resources.displayMetrics.heightPixels
            val twoThirds = (screenHeight * 0.66).toInt()
            peekHeight = twoThirds

            state = BottomSheetBehavior.STATE_HIDDEN

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    overlay.visibility =
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) View.GONE
                        else View.VISIBLE
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    overlay.alpha = ((slideOffset + 1) / 2).coerceIn(0f, 1f)
                }
            })
        }

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("track", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("track") as? Track
        }

        track?.let {
            currentTrack = it
            trackTitle.text = it.trackName
            artistName.text = it.artistName
            it.previewUrl?.let { url -> playerViewModel.preparePlayer(url) }
            Log.d("PlayerActivity", "Cover URL: ${it.getCoverArtwork()}")
            Glide.with(this)
                .load(it.getCoverArtwork())
                .placeholder(R.drawable.placeholder_image)
                .into(findViewById(R.id.album_cover))

            findViewById<TextView>(R.id.info_album_value).text = it.collectionName ?: "Unknown Album"
            findViewById<TextView>(R.id.info_year_value).text = it.getReleaseYear()
            findViewById<TextView>(R.id.info_genre_value).text = it.primaryGenreName ?: "Unknown Genre"
            findViewById<TextView>(R.id.info_country_value).text = it.country ?: "Unknown Country"
            findViewById<TextView>(R.id.info_duration_value).text = it.formatTrackTime(it.trackTimeMillis)
            updateFavoriteButton()
        }

        likeButton.setOnClickListener { toggleFavorite() }
        likePressedButton.setOnClickListener { toggleFavorite() }

        val bottomSheetRecyclerView = findViewById<RecyclerView>(R.id.rvPlaylistsBottomSheet)
        bottomSheetRecyclerView.layoutManager = LinearLayoutManager(this)
        bottomSheetAdapter = BottomSheetPlaylistAdapter { playlist ->
            playerViewModel.addTrackToPlaylist(currentTrack, playlist) { message, added ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                if (added) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
        bottomSheetRecyclerView.adapter = bottomSheetAdapter

        findViewById<ImageView>(R.id.add_button).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            playerViewModel.loadPlaylists()
            playerViewModel.playlistsFlow
                .onEach { bottomSheetAdapter.setPlaylists(it) }
                .launchIn(lifecycleScope)
        }

        val newPlaylistButton = findViewById<Button>(R.id.button_new_playlist)
        newPlaylistButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("open_create_playlist", true)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun updateFavoriteButton() {
        if (currentTrack.isFavorite) {
            likeButton.visibility = View.GONE
            likePressedButton.visibility = View.VISIBLE
        } else {
            likeButton.visibility = View.VISIBLE
            likePressedButton.visibility = View.GONE
        }
    }

    private fun toggleFavorite() {
        currentTrack.isFavorite = !currentTrack.isFavorite
        updateFavoriteButton()

        playerViewModel.updateFavorite(currentTrack)
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

        backButton.setOnClickListener { finish() }

        playButton.setDebouncedOnClickListener(300L, lifecycleScope) { playerViewModel.togglePlayback() }
        pauseButton.setDebouncedOnClickListener(300L, lifecycleScope) { playerViewModel.togglePlayback() }
    }

    private fun setupObservers() {
        playerViewModel.isPlaying.observe(this) { isPlaying ->
            playButton.visibility = if (isPlaying) View.GONE else View.VISIBLE
            pauseButton.visibility = if (isPlaying) View.VISIBLE else View.GONE
        }
        playerViewModel.currentTime.observe(this) { time ->
            currentTimeTextView.text = time
        }
    }
}
