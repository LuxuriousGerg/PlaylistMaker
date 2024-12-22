package com.example.playlistmaker.data.repository

import android.util.Log
import com.example.playlistmaker.data.models.TrackDTO
import com.example.playlistmaker.data.network.iTunesApiService
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TrackRepository

class TrackRepositoryImpl(private val apiService: iTunesApiService) : TrackRepository {

    override suspend fun searchTracks(query: String): List<Track> {
        return try {
            val response = apiService.searchTracks(query)
            response.results.map { mapToDomain(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun mapToDomain(dto: TrackDTO): Track {
        return Track(
            trackName = dto.trackName ?: "Unknown Track",
            artistName = dto.artistName ?: "Unknown Artist",
            trackTimeMillis = dto.trackTimeMillis,
            artworkUrl100 = dto.artworkUrl100,
            collectionName = dto.collectionName ?: "Unknown Album",
            releaseDate = dto.releaseDate ?: "Unknown Year",
            primaryGenreName = dto.primaryGenreName ?: "Unknown Genre",
            country = dto.country ?: "Unknown Country",
            previewUrl = dto.previewUrl
        )
    }

}
