package com.example.playlistmaker.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: FavoriteTrackEntity)

    @Delete
    suspend fun deleteTrack(track: FavoriteTrackEntity)

    // Сортируем по убыванию addedAt, чтобы последние добавленные были сверху
    @Query("SELECT * FROM favorite_tracks ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteTrackEntity>>

    @Query("SELECT trackId FROM favorite_tracks")
    suspend fun getFavoriteTrackIds(): List<Long>
}
