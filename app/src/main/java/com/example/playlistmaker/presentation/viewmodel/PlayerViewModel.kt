import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactors.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import android.util.Log
import com.example.playlistmaker.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.interactors.FavoritesInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _playlistsFlow = MutableStateFlow<List<Playlist>>(emptyList())
    val playlistsFlow = _playlistsFlow.asStateFlow()

    fun loadPlaylists() {
        viewModelScope.launch {
            playlistInteractor.observeAllPlaylists().collect { loaded ->
                _playlistsFlow.value = loaded
            }
        }
    }

    fun addTrackToPlaylist(
        track: Track,
        playlist: Playlist,
        onResult: (message: String, added: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val isAdded = playlistInteractor.addTrackToPlaylist(playlist.id, track)
            val message = if (isAdded) {
                "Добавлено в плейлист ${playlist.name}"
            } else {
                "Трек уже добавлен в плейлист ${playlist.name}"
            }
            onResult(message, isAdded)
        }
    }


    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> get() = _currentTime

    private var updateTimeJob: Job? = null

    fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(
            url,
            onPrepared = {
                _isPlaying.postValue(false)
                _currentTime.postValue("00:00")
            },
            onCompletion = {
                _isPlaying.postValue(false)
                _currentTime.postValue("00:00")
                updateTimeJob?.cancel()
            }
        )
    }

    fun togglePlayback() {
        if (_isPlaying.value == true) {
            Log.d("PlayerViewModel", "togglePlayback: pausing playback")
            playerInteractor.pause()
            _isPlaying.value = false
            updateTimeJob?.cancel()
        } else {
            Log.d("PlayerViewModel", "togglePlayback: starting playback")
            playerInteractor.play()
            _isPlaying.value = true
            startUpdatingTime()
        }
    }

    private fun startUpdatingTime() {
        updateTimeJob?.cancel()
        updateTimeJob = viewModelScope.launch {
            while (isActive && (_isPlaying.value == true)) {
                val currentPosition = playerInteractor.getCurrentPosition().toLong()
                _currentTime.postValue(formatTrackTime(currentPosition))
                delay(300L)
            }
        }
    }

    override fun onCleared() {
        updateTimeJob?.cancel()
        playerInteractor.release()
        super.onCleared()
    }

    private fun formatTrackTime(trackTimeMillis: Long): String {
        val totalSeconds = trackTimeMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun updateFavorite(track: Track) {
        viewModelScope.launch {
            if (track.isFavorite) {
                favoritesInteractor.addTrack(track)
            } else {
                favoritesInteractor.removeTrack(track)
            }
        }
    }
}
