package com.example.playlistmaker.domain.interactors

import com.example.playlistmaker.domain.models.Track

interface HistoryInteractor {
    fun getHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearHistory()
}
