package com.example.playlistmaker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                if (track.isFavorite) {
                    interactor.removeTrack(track)
                } else {
                    interactor.addTrack(track)
                }
                loadFavorites()
            }
        }
    }

class PlaylistViewModel : ViewModel() {
    // Логика для экрана Плейлистов
}

class LibraryViewModel : ViewModel() {
    // Логика для экрана "Медиатека"
}
