import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.interactors.SearchTracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SearchViewModel(
    private val searchTracksInteractor: SearchTracksInteractor,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Track>>(emptyList())
    val searchResults: StateFlow<List<Track>> = _searchResults.asStateFlow()

    private val _searchHistory = MutableStateFlow<List<Track>>(emptyList())
    val searchHistory: StateFlow<List<Track>> = _searchHistory.asStateFlow()

    private val _errorType = MutableStateFlow<String?>(null)
    val errorType: StateFlow<String?> = _errorType.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val searchQuery = MutableStateFlow("")
    private var lastSearchQuery: String = ""
    private var searchJob: Job? = null

    companion object {
        private const val SEARCH_HISTORY_KEY = "search_history"
        private const val SEARCH_DELAY = 500L
        private const val MIN_QUERY_LENGTH = 3
    }

    init {
        loadSearchHistory()
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQuery
                .debounce(SEARCH_DELAY)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isEmpty()) {
                        Log.d("SearchViewModel", "Очистка поиска, загрузка истории")
                        _searchResults.value = emptyList()
                        _errorType.value = null
                        loadSearchHistory()
                        return@collectLatest
                    }

                    if (query.length < MIN_QUERY_LENGTH) {
                        Log.d("SearchViewModel", "Запрос '$query' слишком короткий")
                        _searchResults.value = emptyList()
                        _errorType.value = null
                        return@collectLatest
                    }

                    if (query == lastSearchQuery) {
                        Log.d("SearchViewModel", "Запрос '$query' не изменился, не отправляем")
                        return@collectLatest
                    }

                    lastSearchQuery = query
                    Log.d("SearchViewModel", "Отправляем запрос: '$query'")

                    searchJob?.cancel()
                    searchJob = launch { searchTracks(query) }
                }
        }
    }

    fun updateQuery(query: String) {
        searchQuery.value = query
    }

    private suspend fun searchTracks(query: String) {
        _isSearching.value = true // Показываем анимацию
        _errorType.value = null
        _searchResults.value = emptyList()

        searchTracksInteractor.search(query).collect { result ->
            result.onSuccess { tracks ->
                _searchResults.value = tracks
                _isSearching.value = false // Скрываем анимацию

                if (tracks.isEmpty()) {
                    _errorType.value = "no_results"
                }
            }.onFailure { error ->
                _searchResults.value = emptyList()
                _errorType.value = "connection"
                _isSearching.value = false
            }
        }
    }


    fun loadSearchHistory() {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null)
        val historyList: List<Track> = if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }

        Log.d("SearchViewModel", "Загружена история поиска: ${historyList.size} элементов")
        _searchHistory.value = historyList
    }
    fun addToSearchHistory(track: Track) {
        val updatedList = _searchHistory.value.toMutableList()

        // Удаляем трек из списка, если он уже там есть (чтобы не было дублей)
        updatedList.remove(track)

        updatedList.add(0, track) // Добавляем в начало списка

        if (updatedList.size > 10) { // Ограничение на 10 последних записей
            updatedList.removeAt(updatedList.size - 1)
        }

        saveSearchHistory(updatedList)
        _searchHistory.value = updatedList
    }


    fun clearHistory() {
        saveSearchHistory(emptyList())
        _searchHistory.value = emptyList()
    }

    private fun saveSearchHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(SEARCH_HISTORY_KEY, json).apply()
        Log.d("SearchViewModel", "История поиска сохранена: ${history.size} элементов")
    }
}
