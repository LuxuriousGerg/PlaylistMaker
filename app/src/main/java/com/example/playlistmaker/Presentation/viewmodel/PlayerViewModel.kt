package com.example.playlistmaker.presentation.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.models.Track

class PlayerViewModel : ViewModel() {
    private val _track = MutableLiveData<Track>()
    val track: LiveData<Track> get() = _track

    private val _isPlaying = MutableLiveData<Boolean>(false)
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _currentTime = MutableLiveData<String>("00:00")
    val currentTime: LiveData<String> get() = _currentTime

    private var mediaPlayer: MediaPlayer? = null

    fun preparePlayer(track: Track) {
        _track.value = track
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(track.previewUrl ?: "")
                prepareAsync()
                setOnPreparedListener {
                    _isPlaying.value = false
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentTime.value = "00:00"
                }
            } catch (e: Exception) {
                _isPlaying.value = false
            }
        }
    }

    fun togglePlayback() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _isPlaying.value = false
            } else {
                it.start()
                _isPlaying.value = true
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}