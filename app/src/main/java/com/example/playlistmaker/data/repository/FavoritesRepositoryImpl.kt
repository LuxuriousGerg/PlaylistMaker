package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.FavoriteTrackDao
import com.example.playlistmaker.data.db.FavoriteTrackEntity
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(private val dao: FavoriteTrackDao) : FavoritesRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        dao.insertTrack(FavoriteTrackEntity.fromTrack(track))
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        dao.deleteTrack(FavoriteTrackEntity.fromTrack(track))
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return dao.getAllFavorites().map { list -> list.map { it.toTrack() } }
    }

    override suspend fun getFavoriteTrackIds(): List<Long> {
        return dao.getFavoriteTrackIds()
    }
}
