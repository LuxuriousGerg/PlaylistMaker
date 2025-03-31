package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.PlaylistDao
import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.data.db.PlaylistTrackDao
import com.example.playlistmaker.data.db.PlaylistTrackEntity
import com.example.playlistmaker.data.db.toDomain
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao
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

    suspend fun updatePlaylist(
        playlistId: Long,
        newName: String,
        newDescription: String,
        newCoverFilePath: String?
    ) {
        val entity = playlistDao.getPlaylistById(playlistId)
            ?: return  // Если плейлист не найден — выходим

        val updatedEntity = entity.copy(
            name = newName,
            description = newDescription,
            coverFilePath = newCoverFilePath ?: entity.coverFilePath
        )

        playlistDao.updatePlaylist(updatedEntity)
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
            false
        } else {
            currentList.add(track.trackId.toString())
            val updatedJson = Gson().toJson(currentList)
            val updatedPlaylist = playlistEntity.copy(
                trackIdsJson = updatedJson,
                trackCount = playlistEntity.trackCount + 1
            )
            playlistDao.updatePlaylist(updatedPlaylist)

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
            playlistTrackDao.insertPlaylistTrack(playlistTrackEntity)
            true
        }
    }

    suspend fun getPlaylistById(playlistId: Long): Playlist? {
        val entity = playlistDao.getPlaylistById(playlistId) ?: return null
        return entity.toDomain()
    }

    suspend fun getTracksForPlaylist(playlistId: Long): List<Track> {
        val playlistEntity = playlistDao.getPlaylistById(playlistId) ?: return emptyList()

        val type = object : TypeToken<List<String>>() {}.type
        val trackIds = Gson().fromJson<List<String>>(playlistEntity.trackIdsJson, type) ?: emptyList()
        if (trackIds.isEmpty()) return emptyList()

        val trackLongIds = trackIds.mapNotNull { it.toLongOrNull() }
        if (trackLongIds.isEmpty()) return emptyList()

        val tracksEntities = playlistTrackDao.getTracksByIds(trackLongIds)
        return tracksEntities.map { it.toDomain() }
    }

    suspend fun deletePlaylist(playlistId: Long) {
        val playlistEntity = playlistDao.getPlaylistById(playlistId) ?: return

        val type = object : TypeToken<List<String>>() {}.type
        val trackIds = Gson().fromJson<List<String>>(playlistEntity.trackIdsJson, type) ?: emptyList()

        playlistDao.deletePlaylist(playlistEntity)

        trackIds.forEach { trackIdStr ->
            val trackIdLong = trackIdStr.toLongOrNull() ?: return@forEach
            if (!isTrackUsedInAnyPlaylist(trackIdLong)) {
                playlistTrackDao.deleteTrack(trackIdLong)
            }
        }
    }

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        val playlistEntity = playlistDao.getPlaylistById(playlistId) ?: return

        val type = object : TypeToken<MutableList<String>>() {}.type
        val currentList: MutableList<String> =
            Gson().fromJson(playlistEntity.trackIdsJson, type) ?: mutableListOf()

        val removed = currentList.remove(trackId.toString())
        if (!removed) {
            return
        }

        val updatedJson = Gson().toJson(currentList)
        val updatedPlaylist = playlistEntity.copy(
            trackIdsJson = updatedJson,
            trackCount = playlistEntity.trackCount - 1
        )
        playlistDao.updatePlaylist(updatedPlaylist)

        if (!isTrackUsedInAnyPlaylist(trackId)) {
            playlistTrackDao.deleteTrack(trackId)
        }
    }

    private suspend fun isTrackUsedInAnyPlaylist(trackId: Long): Boolean {
        val allPlaylists = playlistDao.getAllPlaylistsOnce()
        val type = object : TypeToken<List<String>>() {}.type

        for (pl in allPlaylists) {
            val listIds = Gson().fromJson<List<String>>(pl.trackIdsJson, type) ?: emptyList()
            if (trackId.toString() in listIds) {
                return true
            }
        }
        return false
    }
}
