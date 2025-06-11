package ru.vodolatskii.movies.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.data.dto.toMovieList
import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.domain.Repository
import ru.vodolatskii.movies.presentation.utils.UIState


class MoviesViewModel(
    private val repository: Repository,
) : ViewModel() {

    private val _isSearchViewVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSearchViewVisible: LiveData<Boolean> = _isSearchViewVisible

    private val _homeState = MutableStateFlow<UIState>(UIState.Loading)
    val homeState: StateFlow<UIState> = _homeState

    private val _favoriteState = MutableStateFlow<UIState>(UIState.Loading)
    val favoriteState: StateFlow<UIState> = _favoriteState

    var cachePopularMovieList: MutableList<Movie> = emptyList<Movie>().toMutableList()
    var cacheFavoriteMovieList: MutableList<Movie> = emptyList<Movie>().toMutableList()


    fun switchSearchViewVisibility(state: Boolean) {
        _isSearchViewVisible.value = state
    }

    fun getPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _homeState.value = UIState.Loading

                if (cachePopularMovieList.isEmpty()) {
                    repository.getPopularMovieInfo()?.let {
                        cachePopularMovieList = it.toMovieList()
                        _homeState.value = UIState.Success(cachePopularMovieList)
                    } ?: let {
                        _homeState.value = UIState.Error("Сервер вернул пустой ответ!")
                    }
                } else if (cachePopularMovieList.size <= App.instance.loadPopularMoviesLimit) {
                    _homeState.value = UIState.Success(cachePopularMovieList)
                }

            } catch (e: Exception) {
                _homeState.value = UIState.Error("Ошибка запроса - $e")
            }
        }
    }


    fun getFavoriteMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _favoriteState.value = UIState.Loading

                if (cacheFavoriteMovieList.isEmpty()) {
                    repository.getAllMoviesFromFavorites()?.let {
                        cacheFavoriteMovieList = it.toMutableList()

                        _favoriteState.value = UIState.Success(cacheFavoriteMovieList)
                    } ?: let {
                        _favoriteState.value = UIState.Error("В избранном пока ничего нет")
                    }
                } else {
                    _favoriteState.value = UIState.Success(cacheFavoriteMovieList)
                }

            } catch (e: Exception) {
                _favoriteState.value = UIState.Error("Ошибка запроса в базу - $e")
            }
        }
    }


    fun addMovieToFavorite(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {

            val fav = repository.getAllMoviesFromFavorites()

            if (fav.isNullOrEmpty() || !fav.any { movie.movieId == it.movieId && movie.name == it.name }) {
                repository.insertMovieToFavorites(movie)
                cacheFavoriteMovieList.add(movie)

                cachePopularMovieList =
                    cachePopularMovieList.filter { movie.movieId != it.movieId && movie.name != it.name }
                        .toMutableList()

                _homeState.value = UIState.Success(cachePopularMovieList)
                _favoriteState.value = UIState.Success(cacheFavoriteMovieList)
            }

            cachePopularMovieList =
                cachePopularMovieList.filter { movie.movieId != it.movieId && movie.name != it.name }
                    .toMutableList()
            _homeState.value = UIState.Success(cachePopularMovieList)
        }
    }


    fun deleteMovieFromFavorite(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMovieFromFavorites(movie)
            cacheFavoriteMovieList =
                cacheFavoriteMovieList.filter { movie.movieId != it.movieId && movie.name != it.name }
                    .toMutableList()
            if (!cachePopularMovieList.any { movie.movieId == it.movieId && movie.name == it.name }) {
                cachePopularMovieList.add(movie)
//                _homeState.value = UIState.Success(cachePopularMovieList)
            }
        }
    }


    fun deleteFromPopular(movie: Movie) {
        cachePopularMovieList =
            cachePopularMovieList.filter { movie.movieId != it.movieId && movie.name != it.name }
                .toMutableList()
    }
}

