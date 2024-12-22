package com.example.playlistmaker.domain.interactors

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TrackRepository

interface SearchTracksInteractor {
    suspend fun search(query: String): List<Track>
}

class SearchTracksInteractorImpl(private val repository: TrackRepository) : SearchTracksInteractor {
    override suspend fun search(query: String): List<Track> {
        return repository.searchTracks(query)
    }
}