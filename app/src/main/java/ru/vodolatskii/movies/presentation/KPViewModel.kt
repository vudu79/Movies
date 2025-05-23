package ru.vodolatskii.movies.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.data.repository.Repository


class KPViewModel(
    private val repository: Repository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState

    init {
        loadPosters()
    }

    fun loadPosters() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e("mytag", "Поймал $exception")
        }

        viewModelScope.launch(handler) {
            try {
                _uiState.value =
                    UIState.Loading

                repository.getMovieInfo()?.let {
                    _uiState.value =
                        UIState.Success(listDoc = it.docs)

                } ?: let {
                    _uiState.value = UIState.Error("Ошибка при загрузке постеров!")
                }
            } catch (e: Exception) {
                _uiState.value = UIState.Error("Ошибка запроса - $e")
            }
        }
    }
}

