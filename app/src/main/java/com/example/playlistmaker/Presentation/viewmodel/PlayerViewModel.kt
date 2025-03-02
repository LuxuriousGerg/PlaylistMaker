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

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> get() = _currentTime

    private var updateTimeJob: Job? = null

    fun preparePlayer(url: String) {
        Log.d("PlayerViewModel", "preparePlayer called with URL: $url")
        playerInteractor.preparePlayer(url, onPrepared = {
            Log.d("PlayerViewModel", "Player prepared")
            _isPlaying.postValue(false)
            _currentTime.postValue("00:00")
        }, onCompletion = {
            Log.d("PlayerViewModel", "Playback completed")
            _isPlaying.postValue(false)
            _currentTime.postValue("00:00")
            updateTimeJob?.cancel() // Останавливаем обновление времени при завершении воспроизведения
        })
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
        Log.d("PlayerViewModel", "startUpdatingTime: Cancelling previous job if exists")
        updateTimeJob?.cancel()
        updateTimeJob = viewModelScope.launch {
            Log.d("PlayerViewModel", "startUpdatingTime: Update job started")
            while (isActive && (_isPlaying.value ?: false)) {
                val currentPosition = playerInteractor.getCurrentPosition().toLong()
                _currentTime.value = formatTrackTime(currentPosition)
                Log.d("PlayerViewModel", "Current position: $currentPosition ms")
                delay(300L)
            }
            Log.d("PlayerViewModel", "startUpdatingTime: Update job ended")
        }
    }

    override fun onCleared() {
        Log.d("PlayerViewModel", "onCleared: Cancelling updateTimeJob and releasing player")
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
}
