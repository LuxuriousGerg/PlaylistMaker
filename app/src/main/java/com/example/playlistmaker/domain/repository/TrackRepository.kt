package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Track

interface TrackRepository {
    suspend fun searchTracks(query: String): List<Track>
}
