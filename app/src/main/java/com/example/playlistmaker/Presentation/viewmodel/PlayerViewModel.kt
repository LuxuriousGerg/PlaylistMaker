import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.interactors.PlayerInteractor

class PlayerViewModel(private val playerInteractor: PlayerInteractor) : ViewModel() {

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> get() = _currentTime

    private val handler = Handler(Looper.getMainLooper())
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            _currentTime.postValue(formatTrackTime(playerInteractor.getCurrentPosition().toLong()))
            handler.postDelayed(this, 1000) // Обновляем каждую секунду
        }
    }

    fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(url, onPrepared = {
            _isPlaying.postValue(false)
            _currentTime.postValue("00:00")
        }, onCompletion = {
            _isPlaying.postValue(false)
            _currentTime.postValue("00:00")
            handler.removeCallbacks(updateTimeRunnable) // Останавливаем обновление времени
        })
    }

    fun togglePlayback() {
        if (_isPlaying.value == true) {
            playerInteractor.pause()
            _isPlaying.postValue(false)
            handler.removeCallbacks(updateTimeRunnable) // Остановить обновление таймера
        } else {
            playerInteractor.play()
            _isPlaying.postValue(true)
            handler.post(updateTimeRunnable) // Запуск обновления таймера
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(updateTimeRunnable)
        playerInteractor.release()
    }

    private fun formatTrackTime(trackTimeMillis: Long): String {
        val totalSeconds = trackTimeMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}
