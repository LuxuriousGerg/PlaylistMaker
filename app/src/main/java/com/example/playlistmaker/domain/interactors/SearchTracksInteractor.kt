package com.example.playlistmaker.domain.interactors

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.FavoritesRepository
import com.example.playlistmaker.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface SearchTracksInteractor {
    fun search(query: String): Flow<Result<List<Track>>>
}

class SearchTracksInteractorImpl(
    private val repository: TrackRepository,
    private val favoritesRepository: FavoritesRepository
) : SearchTracksInteractor {
    override fun search(query: String): Flow<Result<List<Track>>> = flow {
        repository.searchTracks(query).collect { result ->
            result.onSuccess { tracks ->
                val favoriteIds = favoritesRepository.getFavoriteTrackIds()
                tracks.forEach { track ->
                    if (track.trackId in favoriteIds) {
                        track.isFavorite = true
                    }
                }
                emit(Result.success(tracks))
            }.onFailure {
                emit(Result.failure(it))
            }
        }
    }
}
