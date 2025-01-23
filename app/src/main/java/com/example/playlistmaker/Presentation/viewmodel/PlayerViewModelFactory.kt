package com.example.playlistmaker.presentation.viewmodel

import PlayerViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.domain.interactors.PlayerInteractor

class PlayerViewModelFactory(private val playerInteractor: PlayerInteractor) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(playerInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
