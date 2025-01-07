package com.example.playlistmaker.domain.interactors

import com.example.playlistmaker.domain.repository.HistoryRepository
import com.example.playlistmaker.domain.models.Track

class HistoryInteractorImpl(private val repository: HistoryRepository) : HistoryInteractor {

    override fun getHistory(): List<Track> {
        return repository.getHistory() // Репозиторий возвращает уже преобразованные объекты Track
    }

    override fun addTrackToHistory(track: Track) {
        repository.addTrack(track) // Репозиторий преобразует Track в HistoryTrackDTO
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}
