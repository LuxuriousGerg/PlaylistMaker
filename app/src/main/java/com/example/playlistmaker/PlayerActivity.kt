package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide


class PlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.audio_player)

        val backButton: ImageButton = findViewById(R.id.back_button)

        backButton.setOnClickListener {
            finish()
        }

        // Получаем данные трека из интента
        val track = intent.getSerializableExtra("track") as? Track

        track?.let {
            // Название трека и исполнитель
            findViewById<TextView>(R.id.track_title).text = it.trackName
            findViewById<TextView>(R.id.artist_name).text = it.artistName
            findViewById<TextView>(R.id.info_duration_value).text = formatTrackTime(it.trackTimeMillis)
            findViewById<TextView>(R.id.info_album_value).text = it.collectionName
            findViewById<TextView>(R.id.info_year_value).text = it.getReleaseYear()
            findViewById<TextView>(R.id.info_genre_value).text = it.primaryGenreName
            findViewById<TextView>(R.id.info_country_value).text = it.country

            // Загрузка изображения обложки альбома
            Glide.with(this)
                .load(it.getCoverArtwork())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error)
                .into(findViewById(R.id.album_cover))
        } ?: run {
            // Обработка случая, если данные трека не были переданы
            findViewById<TextView>(R.id.track_title).text = "Неизвестный трек"
            findViewById<TextView>(R.id.artist_name).text = "Неизвестный исполнитель"
            findViewById<TextView>(R.id.info_duration_value).text = "00:00"
            findViewById<TextView>(R.id.info_album_value).text = "Неизвестный альбом"
            findViewById<TextView>(R.id.info_year_value).text = "-"
            findViewById<TextView>(R.id.info_genre_value).text = "-"
            findViewById<TextView>(R.id.info_country_value).text = "-"
            Glide.with(this)
                .load(R.drawable.placeholder_image)
                .into(findViewById(R.id.album_cover))
        }
    }
}

