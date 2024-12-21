package com.example.playlistmaker.domain.interactors

import com.example.playlistmaker.data.dto.Track

interface HistoryInteractor {
    fun getHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearHistory()
}
