package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTracks(query: String): Flow<Result<List<Track>>>
}
