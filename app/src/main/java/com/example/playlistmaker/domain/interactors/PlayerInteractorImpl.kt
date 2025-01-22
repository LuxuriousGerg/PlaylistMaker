package com.example.playlistmaker.domain.interactors

import com.example.playlistmaker.domain.repository.PlayerRepository

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {

    override fun preparePlayer(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit) {
        playerRepository.preparePlayer(url, onPrepared, onCompletion)
    }

    override fun play() { playerRepository.play() }
    override fun pause() { playerRepository.pause() }
    override fun release() { playerRepository.release() }
    override fun isPlaying(): Boolean = playerRepository.isPlaying()
    override fun getCurrentPosition(): Int = playerRepository.getCurrentPosition()
}
