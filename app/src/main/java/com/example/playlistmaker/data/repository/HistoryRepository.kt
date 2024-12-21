package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.data.dto.Track

interface HistoryRepository {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}
