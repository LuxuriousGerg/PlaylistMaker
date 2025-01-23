package com.example.playlistmaker.domain.repository

interface PlayerRepository {
    fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
    fun play()
    fun pause()
    fun release()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
}
