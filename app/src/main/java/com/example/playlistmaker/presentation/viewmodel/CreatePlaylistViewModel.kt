package com.example.playlistmaker.presentation

import android.net.Uri
import androidx.lifecycle.*
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    var playlistName: String = ""
    var playlistDescription: String = ""
    var coverUri: Uri? = null
    var hasUnsavedData: Boolean = false

    private val _playlistCreatedEvent = MutableLiveData<String?>()
    val playlistCreatedEvent: LiveData<String?> = _playlistCreatedEvent

    fun onCreatePlaylistClicked() {
        if (playlistName.isBlank()) return
        viewModelScope.launch {
            playlistInteractor.saveCoverAndCreatePlaylist(
                playlistName,
                playlistDescription,
                coverUri
            )
            _playlistCreatedEvent.value = playlistName
        }
    }
}
