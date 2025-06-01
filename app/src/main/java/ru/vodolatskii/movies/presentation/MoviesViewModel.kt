package ru.vodolatskii.movies.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.data.dto.toMovieList
import ru.vodolatskii.movies.data.repository.interfaces.Repository
import ru.vodolatskii.movies.presentation.utils.UIState


class MoviesViewModel(
    private val repository: Repository,
) : ViewModel() {

    private val _homeState = MutableStateFlow<UIState>(UIState.Loading)
    val homeState: StateFlow<UIState> = _homeState

    private val _favoriteState = MutableStateFlow<UIState>(UIState.Loading)
    val favoriteState: StateFlow<UIState> = _favoriteState

    init {
        getPopularMovies()
        getFavoriteMovies()
    }

    fun getPopularMovies() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e("mytag", "Поймал ексепшн в корутине --- $exception")
        }

        viewModelScope.launch(handler) {
            try {
                _homeState.value =
                    UIState.Loading
                repository.getMovieInfo()?.let {
                    _homeState.value =
                        UIState.Success( it.toMovieList())
                } ?: let {
                    _homeState.value = UIState.Error("Сервер вернул пустой ответ!")
                }
            } catch (e: Exception) {
                _homeState.value = UIState.Error("Ошибка запроса - $e")
            }
        }
    }

    fun getFavoriteMovies() {
        val handler = CoroutineExceptionHandler { _, exception ->
            Log.e("mytag", "Поймал ексепшн в корутине --- $exception")
        }

        viewModelScope.launch(handler) {
            try {
                _favoriteState.value =
                    UIState.Loading
                repository.getAllDocsFromDB()?.let {
                    _favoriteState.value =
                        UIState.Success( it)

                } ?: let {
                    _favoriteState.value = UIState.Error("Сервер вернул пустой ответ!")
                }
            } catch (e: Exception) {
                _favoriteState.value = UIState.Error("Ошибка запроса - $e")
            }
        }
    }
}

