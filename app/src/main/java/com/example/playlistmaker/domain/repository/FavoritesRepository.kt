package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addTrackToFavorites(track: Track)
    suspend fun removeTrackFromFavorites(track: Track)
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun getFavoriteTrackIds(): List<Long>
}
