package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaylistTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistTrack(track: PlaylistTrackEntity)
    // Получить список треков, у которых trackId входит в список
    @Query("SELECT * FROM playlist_tracks WHERE trackId IN (:ids)")
    suspend fun getTracksByIds(ids: List<Long>): List<PlaylistTrackEntity>

    // Удалить трек по trackId
    @Query("DELETE FROM playlist_tracks WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Long)
}