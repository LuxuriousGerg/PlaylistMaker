package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.data.dto.Track

interface TrackRepository {
    suspend fun searchTracks(query: String): List<Track>
}
