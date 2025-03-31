package com.example.playlistmaker.presentation

import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : CreatePlaylistViewModel(playlistInteractor) {

    var playlistId: Long? = null

    fun onSavePlaylistClicked() {
        if (playlistName.isBlank()) return

        viewModelScope.launch {
            playlistId?.let { id ->
                playlistInteractor.saveCoverAndUpdatePlaylist(
                    playlistId = id,
                    newName = playlistName,
                    newDescription = playlistDescription,
                    newCoverUri = coverUri
                )
            }

            _playlistCreatedEvent.value = playlistName
        }
    }
}
