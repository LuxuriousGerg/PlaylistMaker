package com.example.playlistmaker.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.domain.repository.PlayerRepository

class PlayerRepositoryImpl : PlayerRepository {
    private var mediaPlayer: MediaPlayer? = null

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener { onPrepared() }
            setOnCompletionListener { onCompletion() }
            prepareAsync()
        }
    }

    override fun play() { mediaPlayer?.start() }
    override fun pause() { mediaPlayer?.pause() }
    override fun release() { mediaPlayer?.release(); mediaPlayer = null }
    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false
    override fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0
}
