package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.FavoriteTrackDao
import com.example.playlistmaker.data.db.FavoriteTrackEntity
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val favoriteTrackDao: FavoriteTrackDao
) : FavoritesRepository {

    override suspend fun addTrackToFavorites(track: Track) {
        favoriteTrackDao.insertTrack(
            FavoriteTrackEntity(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100 ?: "",
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl,
                addedAt = System.currentTimeMillis() // отметка времени
            )
        )
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        favoriteTrackDao.deleteTrack(
            FavoriteTrackEntity(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100 ?: "",
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl,
                addedAt = 0 // здесь не принципиально
            )
        )
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTrackDao.getAllFavorites().map { entities ->
            entities.map { entity ->
                Track(
                    trackId = entity.trackId,
                    trackName = entity.trackName,
                    artistName = entity.artistName,
                    trackTimeMillis = entity.trackTimeMillis,
                    artworkUrl100 = entity.artworkUrl100,
                    collectionName = entity.collectionName,
                    releaseDate = entity.releaseDate,
                    primaryGenreName = entity.primaryGenreName,
                    country = entity.country,
                    previewUrl = entity.previewUrl,
                    isFavorite = true
                )
            }
        }
    }

    override suspend fun getFavoriteTrackIds(): List<Long> {
        return favoriteTrackDao.getFavoriteTrackIds()
    }
}
