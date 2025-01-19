package com.example.playlistmaker.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactors.SearchTracksInteractor
import com.example.playlistmaker.domain.interactors.HistoryInteractor
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchTracksInteractor: SearchTracksInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {

    private val _searchResults = MutableLiveData<List<Track>>()
    val searchResults: LiveData<List<Track>> get() = _searchResults

    private val _searchHistory = MutableLiveData<List<Track>>()
    val searchHistory: LiveData<List<Track>> get() = _searchHistory

    private val _errorType = MutableLiveData<String?>()
    val errorType: LiveData<String?> get() = _errorType

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun searchTracks(query: String) {
        viewModelScope.launch {
            searchTracksInteractor.search(query).collect { result ->
                result.onSuccess { tracks ->
                    _searchResults.value = tracks
                }.onFailure { error ->
                    _searchResults.value = emptyList()
                }
            }
        }
    }

    fun loadSearchHistory() {
        _searchHistory.value = historyInteractor.getHistory()
    }

    fun clearHistory() {
        historyInteractor.clearHistory()
        _searchHistory.value = emptyList()
    }

    fun addToSearchHistory(track: Track) {
        historyInteractor.addTrackToHistory(track)
        _searchHistory.value = historyInteractor.getHistory()
    }
}
