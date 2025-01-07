package com.example.playlistmaker.data.repository

import android.content.SharedPreferences
import com.example.playlistmaker.data.dto.HistoryTrackDTO
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.repository.HistoryRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) : HistoryRepository {

    private val gson = Gson()
    private val key = "search_history"

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(key, null) ?: return emptyList()
        val type = object : TypeToken<List<HistoryTrackDTO>>() {}.type
        val dtoList: List<HistoryTrackDTO> = gson.fromJson(json, type) ?: emptyList()
        return dtoList.map { mapToDomain(it) }
    }

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeIf { it.trackName == track.trackName }
        history.add(0, track)
        if (history.size > 10) {
            history.removeAt(history.size - 1)
        }
        saveHistory(history.map { mapToDTO(it) })
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(key).apply()
    }

    private fun saveHistory(history: List<HistoryTrackDTO>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(key, json).apply()
    }

    private fun mapToDomain(dto: HistoryTrackDTO): Track {
        return Track(
            trackName = dto.trackName,
            artistName = dto.artistName,
            trackTimeMillis = dto.trackTimeMillis,
            artworkUrl100 = dto.artworkUrl100,
            collectionName = dto.collectionName,
            releaseDate = dto.releaseDate,
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            previewUrl = dto.previewUrl
        )
    }

    private fun mapToDTO(track: Track): HistoryTrackDTO {
        return HistoryTrackDTO(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }
}
