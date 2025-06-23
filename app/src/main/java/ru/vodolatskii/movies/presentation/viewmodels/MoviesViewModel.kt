package ru.vodolatskii.movies.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.data.entity.dto.ErrorResponseDto
import ru.vodolatskii.movies.domain.MovieRepository
import ru.vodolatskii.movies.presentation.utils.UIState
import javax.inject.Inject


class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository,
) : ViewModel() {

    val categoryPropertyLifeData: MutableLiveData<String> = MutableLiveData()

    private val _isSearchViewVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSearchViewVisible: LiveData<Boolean> = _isSearchViewVisible

    private val _homeState = MutableStateFlow<UIState>(UIState.Loading)
    val homeState: StateFlow<UIState> = _homeState

    private val _favoriteState = MutableStateFlow<UIState>(UIState.Loading)
    val favoriteState: StateFlow<UIState> = _favoriteState

    var cachePopularMovieList: MutableList<Movie> = mutableListOf()
    var cacheFavoriteMovieList: MutableList<Movie> = mutableListOf()

    var loadedPages: MutableSet<Int> = mutableSetOf()

    var pageCount = 1
        private set


    init {
        getCategoryProperty()
    }


    fun switchSearchViewVisibility(state: Boolean) {
        _isSearchViewVisible.value = state
    }

    fun getPopularMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _homeState.value = UIState.Loading

                if (!loadedPages.contains(pageCount) || cachePopularMovieList.isEmpty()) {

                    repository.getPopularMovieTMDBResponse(
                        page = pageCount,
                        callback = object : ApiCallback {
                            override fun onSuccess(films: MutableList<Movie>) {
                                cachePopularMovieList.addAll(films)
                                _homeState.value = UIState.Success(cachePopularMovieList)
                                loadedPages.add(pageCount)
                            }
                            override fun onFailure(error: ErrorResponseDto) {
                                _homeState.value = UIState.Error("Response code - ${error.statusCode}\n${error.message}")
                            }
                        })
                } else {
                    _homeState.value = UIState.Success(cachePopularMovieList)
                }

            } catch (e: Exception) {
                _homeState.value = UIState.Error("Error - $e")
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


    fun plusPageCount() {
        pageCount += 1
    }


    private fun getCategoryProperty() {
        //Кладем категорию в LiveData
        categoryPropertyLifeData.value = repository.getDefaultCategoryFromPreferences()
    }

    fun putCategoryProperty(category: String) {
        //Сохраняем в настройки
        repository.saveDefaultCategoryToPreferences(category)
        //И сразу забираем, чтобы сохранить состояние в модели
        getCategoryProperty()
    }


    interface ApiCallback {
        fun onSuccess(films: MutableList<Movie>)
        fun onFailure(error: ErrorResponseDto)
    }
}

