package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.Track
import com.example.playlistmaker.data.network.iTunesApiService
import com.example.playlistmaker.domain.repository.TrackRepository

class TrackRepositoryImpl(private val apiService: iTunesApiService) : TrackRepository {

    override suspend fun searchTracks(query: String): List<Track> {
        return try {
            val response = apiService.searchTracks(query)
            response.results
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
