package ru.vodolatskii.movies.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.data.dto.toMovieList
import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.data.repository.interfaces.Repository
import ru.vodolatskii.movies.presentation.utils.UIState


class MoviesViewModel(
    private val repository: Repository,
) : ViewModel() {

    private val _homeState = MutableStateFlow<UIState>(UIState.Loading)
    val homeState: StateFlow<UIState> = _homeState

    private val _favoriteState = MutableStateFlow<UIState>(UIState.Loading)
    val favoriteState: StateFlow<UIState> = _favoriteState

    private var cacheMovieList: MutableList<Movie> = emptyList<Movie>().toMutableList()

    init {
        getPopularMovies()
        getFavoriteMovies()
    }

    private fun getPopularMovies() {
//        val handler = CoroutineExceptionHandler { _, exception ->
//            Log.e("mytag", "Поймал ексепшн в корутине --- $exception")
//        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _homeState.value = UIState.Loading
                if (cacheMovieList.isEmpty()) {
                    repository.getMovieInfo()?.let {
                        cacheMovieList = it.toMovieList()
                        _homeState.value = UIState.Success(cacheMovieList)
                    } ?: let {
                        _homeState.value = UIState.Error("Сервер вернул пустой ответ!")
                    }
                } else {
                    _homeState.value = UIState.Success(cacheMovieList)
                }
            } catch (e: Exception) {
                _homeState.value = UIState.Error("Ошибка запроса - $e")
            }
        }
    }

    fun addMovieToFavorite(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            val fav = repository.getAllMoviesFromFavorites()
            if (fav.isNullOrEmpty() || !fav.any { movie.movieId == it.movieId && movie.name == it.name }) {
                repository.insertMovieToFavorites(movie)
                cacheMovieList= cacheMovieList.filter { movie.movieId != it.movieId && movie.name != it.name  }.toMutableList()
                _homeState.value = UIState.Success(cacheMovieList)
            }
            cacheMovieList.remove(movie)
            _homeState.value = UIState.Success(cacheMovieList)
        }
    }

    fun deleteMovieFromFavorite(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMovieFromFavorites(movie)
            if (!cacheMovieList.any { movie.movieId == it.movieId && movie.name == it.name }) {
                cacheMovieList.add(movie)
                _homeState.value = UIState.Success(cacheMovieList)
            }
        }
    }

    fun getFavoriteMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _favoriteState.value =
                    UIState.Loading
                repository.getAllMoviesFromFavorites()?.let {
                    _favoriteState.value =
                        UIState.Success(it)
                    it.forEach {
                    }

                } ?: let {
                    _favoriteState.value = UIState.Error("Запрос в базу вернул null")
                }
            } catch (e: Exception) {
                _favoriteState.value = UIState.Error("Ошибка запроса в базу- $e")
            }
        }
    }
}

