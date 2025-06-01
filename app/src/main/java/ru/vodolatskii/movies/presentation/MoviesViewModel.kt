package ru.vodolatskii.movies.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.data.repository.interfaces.Repository
import ru.vodolatskii.movies.presentation.utils.UIState


class MoviesViewModel(
    private val repository: Repository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState

    init {
        loadRandomPosters()
    }

    fun loadRandomPosters() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e("mytag", "Поймал ексепшн в корутине --- $exception")
        }

        viewModelScope.launch(handler) {
            try {
                _uiState.value =
                    UIState.Loading
                repository.getMovieInfo()?.let {
                    _uiState.value =
                        UIState.Success(listDoc = it.docs)
                } ?: let {
                    _uiState.value = UIState.Error("Сервер вернул пустой ответ!")
                }
            } catch (e: Exception) {
                _uiState.value = UIState.Error("Ошибка запроса - $e")
            }
        }
    }
}

