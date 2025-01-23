package com.example.playlistmaker.domain.interactors

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow

interface SearchTracksInteractor {
    fun search(query: String): Flow<Result<List<Track>>>
}

class SearchTracksInteractorImpl(private val repository: TrackRepository) : SearchTracksInteractor {
    override fun search(query: String): Flow<Result<List<Track>>> {
        return repository.searchTracks(query)
    }
}
