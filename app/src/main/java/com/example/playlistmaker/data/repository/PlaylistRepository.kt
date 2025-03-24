package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.PlaylistDao
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.PlaylistTrackDao
import com.example.playlistmaker.data.db.PlaylistTrackEntity
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao // <-- новый параметр
) {

    suspend fun createPlaylist(name: String, description: String?, coverFilePath: String?) {
        val entity = PlaylistEntity(
            name = name,
            description = description.orEmpty(),
            coverFilePath = coverFilePath,
            trackIdsJson = "[]",
            trackCount = 0
        )
        playlistDao.insertPlaylist(entity)
    }

    suspend fun updatePlaylist(entity: PlaylistEntity) {
        playlistDao.updatePlaylist(entity)
    }

    fun getAllPlaylists(): Flow<List<PlaylistEntity>> {
        return playlistDao.getAllPlaylists()
    }

    suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean {
        val playlistEntity = playlistDao.getPlaylistById(playlistId) ?: return false

        val type = object : TypeToken<MutableList<String>>() {}.type
        val currentList: MutableList<String> =
            Gson().fromJson(playlistEntity.trackIdsJson, type) ?: mutableListOf()

        return if (currentList.contains(track.trackId.toString())) {
            // трек уже добавлен
            false
        } else {
            // Добавляем идентификатор трека в JSON-список
            currentList.add(track.trackId.toString())
            val updatedJson = Gson().toJson(currentList)
            val updatedPlaylist = playlistEntity.copy(
                trackIdsJson = updatedJson,
                trackCount = playlistEntity.trackCount + 1
            )
            // Обновляем плейлист в базе
            playlistDao.updatePlaylist(updatedPlaylist)

            // Преобразуем Track в PlaylistTrackEntity и сохраняем его
            val playlistTrackEntity = PlaylistTrackEntity(
                trackId = track.trackId,
                trackName = track.trackName,
                artistName = track.artistName,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                trackTimeMillis = track.trackTimeMillis,
                artworkUrl100 = track.artworkUrl100,
                previewUrl = track.previewUrl
            )
            // Сохраняем трек в таблицу playlist_tracks
            playlistTrackDao.insertPlaylistTrack(playlistTrackEntity)
            true
        }
    }
}
