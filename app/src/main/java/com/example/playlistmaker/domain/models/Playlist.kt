package com.example.playlistmaker.domain.models

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val coverUri: String?, // путь к обложке (может быть null)
    val trackCount: Int
)