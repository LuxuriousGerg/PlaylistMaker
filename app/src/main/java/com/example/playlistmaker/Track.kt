package com.example.playlistmaker

import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: Long, // Продолжительность трека
    val artworkUrl100: String // Ссылка на изображение обложки
)

data class SearchResponse(
    val resultCount: Int, // Количество результатов
    val results: List<Track> // Список треков
)

fun formatTrackTime(trackTimeMillis: Long): String {
    val seconds = trackTimeMillis / 1000
    return SimpleDateFormat("mm:ss", Locale.getDefault()).format(seconds * 1000)
}