package com.example.playlistmaker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.interactors.FavoritesInteractor
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(private val interactor: FavoritesInteractor) : ViewModel() {

    private val _favoriteTracks = MutableStateFlow<List<Track>>(emptyList())
    val favoriteTracks = _favoriteTracks.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            interactor.getAllFavorites().collect { tracks ->
                _favoriteTracks.value = tracks
            }
        }
    }

    fun toggleFavorite(track: Track) {
        viewModelScope.launch {
            // Создаём новый экземпляр трека с инвертированным флагом isFavorite
            val updatedTrack = track.copy(isFavorite = !track.isFavorite)
            if (updatedTrack.isFavorite) {
                interactor.addTrack(updatedTrack)
            } else {
                interactor.removeTrack(updatedTrack)
            }
            // Обновляем список избранных треков после изменения
            loadFavorites()
        }
    }
}

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {
    val playlistsFlow = playlistInteractor.observeAllPlaylists()
}
