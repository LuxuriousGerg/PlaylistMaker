import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.interactors.SearchTracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class SearchViewModel(
    private val searchTracksInteractor: SearchTracksInteractor,
    private val sharedPreferences: SharedPreferences // Добавляем SharedPreferences
) : ViewModel() {

    private val _searchResults = MutableLiveData<List<Track>>()
    val searchResults: LiveData<List<Track>> get() = _searchResults

    private val _searchHistory = MutableLiveData<List<Track>>()
    val searchHistory: LiveData<List<Track>> get() = _searchHistory

    private var historyList = mutableListOf<Track>()

    private val _errorType = MutableLiveData<String?>()
    val errorType: LiveData<String?> get() = _errorType

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
    }

    init {
        loadSearchHistory() // Загружаем историю при создании ViewModel
    }

    suspend fun searchTracks(query: String) {
        searchTracksInteractor.search(query).collect { result ->
            result.onSuccess { tracks ->
                _searchResults.value = tracks
                if (tracks.isEmpty()) {
                    _errorType.value = "no_results" // Показываем "нет результатов"
                } else {
                    _errorType.value = null // Если есть треки, убираем ошибку
                }
            }.onFailure { e ->
                _searchResults.value = emptyList()
                _errorType.value = if (e is IOException) "connection" else "no_results"
            }
        }
    }


    fun loadSearchHistory() {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<Track>>() {}.type
            historyList = Gson().fromJson(json, type) ?: mutableListOf()
        }
        _searchHistory.value = historyList
    }

    fun addToSearchHistory(track: Track) {
        historyList.remove(track)
        historyList.add(0, track)
        if (historyList.size > 10) {
            historyList.removeAt(historyList.size - 1)
        }
        saveSearchHistory()
        _searchHistory.value = historyList
    }

    fun clearHistory() {
        historyList.clear()
        saveSearchHistory()
        _searchHistory.value = emptyList()
    }

    private fun saveSearchHistory() {
        val json = Gson().toJson(historyList)
        sharedPreferences.edit().putString(SEARCH_HISTORY_KEY, json).apply()
    }
}
