package com.example.playlistmaker.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.playlistmaker.domain.models.Track

@Entity(tableName = "playlist_tracks")
data class PlaylistTrackEntity(
    @PrimaryKey
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val trackTimeMillis: Long,
    val artworkUrl100: String?,
    val previewUrl: String?
)
fun PlaylistTrackEntity.toDomain(): Track {
    return Track(
        trackId = this.trackId,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTimeMillis = this.trackTimeMillis,
        artworkUrl100 = this.artworkUrl100,
        collectionName = this.collectionName ?: "",
        releaseDate = this.releaseDate ?: "",
        primaryGenreName = this.primaryGenreName ?: "",
        country = this.country ?: "",
        previewUrl = this.previewUrl,
        isFavorite = false
    )
}