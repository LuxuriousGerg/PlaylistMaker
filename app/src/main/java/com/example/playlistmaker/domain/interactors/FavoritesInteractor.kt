package com.example.playlistmaker.domain.interactors

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow

class FavoritesInteractor(private val repository: FavoritesRepository) {

    suspend fun addTrack(track: Track) = repository.addTrackToFavorites(track)

    suspend fun removeTrack(track: Track) = repository.removeTrackFromFavorites(track)

    fun getAllFavorites(): Flow<List<Track>> = repository.getFavoriteTracks()
}
