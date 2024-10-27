package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageButton
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player)

        val backButton: ImageButton = findViewById(R.id.back_button)

        backButton.setOnClickListener {
            finish()
        }

        // Инициализация элементов
        trackTitle = findViewById(R.id.track_title)
        trackTitle.isSelected = true
        artistName = findViewById(R.id.artist_name)
        artistName.isSelected = true
        durationValue = findViewById(R.id.info_duration_value)
        albumValue = findViewById(R.id.info_album_value)
        yearValue = findViewById(R.id.info_year_value)
        genreValue = findViewById(R.id.info_genre_value)
        countryValue = findViewById(R.id.info_country_value)

        val track = intent.getSerializableExtra("track") as? Track

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
        } ?: run {
            trackTitle.text = getString(R.string.unknown_track)
            artistName.text = getString(R.string.unknown_artist)
            durationValue.text = getString(R.string.default_duration)
            albumValue.text = getString(R.string.unknown_album)
            yearValue.text = getString(R.string.default_year)
            genreValue.text = getString(R.string.default_genre)
            countryValue.text = getString(R.string.default_country)

            Glide.with(this)
                .load(R.drawable.placeholder_image)
                .into(findViewById(R.id.album_cover))
        }
    }
}