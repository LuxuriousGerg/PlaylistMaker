package com.example.playlistmaker.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.data.repository.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlaylistInsideViewModel(
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    data class PlaylistUIModel(
        val id: Long,
        val name: String,
        val description: String,
        val coverUri: String?,
        val trackCount: Int,
        val totalMinutes: String
    )

    private val _playlistUIFlow = MutableStateFlow<PlaylistUIModel?>(null)
    val playlistUIFlow: StateFlow<PlaylistUIModel?> = _playlistUIFlow

    private val _tracksFlow = MutableStateFlow<List<Track>>(emptyList())
    val tracksFlow: StateFlow<List<Track>> = _tracksFlow

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val playlist: Playlist = playlistRepository.getPlaylistById(playlistId) ?: return@launch

            val trackList = playlistRepository.getTracksForPlaylist(playlistId)
            Log.d("PlaylistInsideViewModel", "trackList size = ${trackList.size}")
            _tracksFlow.value = trackList

            val sumMillis = trackList.sumOf { it.trackTimeMillis }
            val minutes = (sumMillis / 1000 / 60).toString()

            val uiModel = PlaylistUIModel(
                id = playlist.id,
                name = playlist.name,
                description = playlist.description,
                coverUri = playlist.coverUri,
                trackCount = playlist.trackCount,
                totalMinutes = minutes
            )
            _playlistUIFlow.value = uiModel
        }
    }

    fun deletePlaylist(playlistId: Long, onDeleted: () -> Unit) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
            onDeleted()
        }
    }

    fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            playlistRepository.removeTrackFromPlaylist(playlistId, trackId)
            loadPlaylist(playlistId)
        }
    }
}
