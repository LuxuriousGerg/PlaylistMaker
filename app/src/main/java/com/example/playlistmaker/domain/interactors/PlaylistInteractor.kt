package com.example.playlistmaker.domain.interactor

import android.content.Context
import android.net.Uri
import com.example.playlistmaker.data.db.toDomain
import com.example.playlistmaker.data.repository.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.FileUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistInteractor(
    private val context: Context,
    private val repository: PlaylistRepository
) {
    suspend fun saveCoverAndCreatePlaylist(
        name: String,
        description: String,
        coverUri: Uri?
    ) {
        val coverFilePath = FileUtils.copyUriToInternalStorage(context, coverUri)
        repository.createPlaylist(name, description, coverFilePath)
    }

    fun observeAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
            .map { list -> list.map { it.toDomain() } }
    }
    suspend fun addTrackToPlaylist(playlistId: Long, track: Track): Boolean {
        return repository.addTrackToPlaylist(playlistId, track)
    }
    suspend fun saveCoverAndUpdatePlaylist(
        playlistId: Long,
        newName: String,
        newDescription: String,
        newCoverUri: Uri?
    ) {
        val newCoverFilePath = newCoverUri?.let {
            FileUtils.copyUriToInternalStorage(context, it)
        }
        repository.updatePlaylist(playlistId, newName, newDescription, newCoverFilePath)
    }
}
