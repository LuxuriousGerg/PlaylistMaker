package com.example.playlistmaker.domain.models

import java.io.Serializable

data class Track(
    val trackId: Long,          // ID
    val trackName: String,      // Название композиции
    val artistName: String,     // Имя исполнителя
    val trackTimeMillis: Long,  // Продолжительность трека
    val artworkUrl100: String?, // Ссылка на изображение обложки
    val collectionName: String, // Название альбома
    val releaseDate: String,    // Год выпуска
    val primaryGenreName: String, // Жанр
    val country: String,        // Страна
    val previewUrl: String?,
    var isFavorite: Boolean = false   // Флаг избранного
) : Serializable {

    fun getReleaseYear(): String {
        return releaseDate.split("-")[0] // Извлекаем только год
    }

    // Функция для получения более качественного изображения обложки
    fun getCoverArtwork(): String {
        return artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
            ?: "@drawable/placeholder_image"
    }

    data class SearchResponse(
        val resultCount: Int, // Количество результатов
        val results: List<Track> // Список треков
    )

    fun formatTrackTime(trackTimeMillis: Long): String {
        val totalSeconds = trackTimeMillis / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
}


