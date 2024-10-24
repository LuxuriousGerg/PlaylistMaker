package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    private val gson = Gson()
    private val key = "search_history"

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(key, null) ?: return emptyList()
        val type = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()

        // Удаляем трек, если он уже есть в истории
        history.removeIf { it.trackName == track.trackName }

        // Добавляем новый трек
        history.add(0, track)

        // Ограничиваем историю до 10 треков
        if (history.size > 10) {
            history.removeAt(history.size - 1)
        }

        saveHistory(history)
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(key).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(key, json).apply()
    }
}
