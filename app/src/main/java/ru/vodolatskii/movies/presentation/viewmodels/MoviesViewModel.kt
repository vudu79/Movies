package ru.vodolatskii.movies.presentation.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.common.SortEvents
import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.data.entity.dto.ErrorResponseDto
import ru.vodolatskii.movies.domain.MovieRepository
import ru.vodolatskii.movies.presentation.utils.UIState
import javax.inject.Inject


class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository,
) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {

    val categoryPropertyLifeData: MutableLiveData<String> = MutableLiveData()
    val requestLanguageLifeData: MutableLiveData<String> = MutableLiveData()

    private val _isSearchViewVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSearchViewVisible: LiveData<Boolean> = _isSearchViewVisible

    private val _homeState = MutableStateFlow<UIState>(UIState.Loading)
    val homeState: StateFlow<UIState> = _homeState

    private val _favoriteState = MutableStateFlow<UIState>(UIState.Loading)
    val favoriteState: StateFlow<UIState> = _favoriteState

    var cachedMovieList: MutableList<Movie> = mutableListOf()
        private set

    var cachedFavoriteMovieList: MutableList<Movie> = mutableListOf()
        private set

    var loadedPages: MutableSet<Int> = mutableSetOf()
        private set

    var pageCount = 1
        private set

    init {
        getCategoryProperty()
        getRequestLanguage()
        repository.getPreference().registerOnSharedPreferenceChangeListener(this)
    }

    fun onSortRVEvents(event: SortEvents){
        when(event){
            SortEvents.ALPHABET -> {
                val sortedList = cachedMovieList.sortedBy {
                    it.name
                }
                _homeState.value = UIState.Success(sortedList)
            }
            SortEvents.DATE -> {
                val sortedList = cachedMovieList.sortedBy {
                    it.releaseDateTimeStump
                }
                _homeState.value = UIState.Success(sortedList)
            }
            SortEvents.RATING -> {
                val sortedList = cachedMovieList.sortedBy {
                    it.rating
                }.reversed()
                _homeState.value = UIState.Success(sortedList)
            }
        }
    }

    fun clearLoadedPages() {
        loadedPages.clear()
    }

    fun clearCachedMovieList() {
        cachedMovieList.clear()
    }

    fun switchSearchViewVisibility(state: Boolean) {
        _isSearchViewVisible.value = state
    }

    fun getMoviesFromApi() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _homeState.value = UIState.Loading

                if (!loadedPages.contains(pageCount) || cachedMovieList.isEmpty()) {

                    repository.getMovieResponceFromTMDBApi(
                        page = pageCount,
                        callback = object : ApiCallback {
                            override fun onSuccess(films: MutableList<Movie>) {
                                cachedMovieList.addAll(films)
                                _homeState.value = UIState.Success(cachedMovieList)
                                loadedPages.add(pageCount)
                            }

                            override fun onFailure(error: ErrorResponseDto) {
                                _homeState.value =
                                    UIState.Error("Response code - ${error.statusCode}\n${error.message}")
                            }
                        })
                } else {
                    _homeState.value = UIState.Success(cachedMovieList)
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

                if (cachedFavoriteMovieList.isEmpty()) {
                    repository.getAllMoviesFromFavorites()?.let {
                        cachedFavoriteMovieList = it.toMutableList()

                        _favoriteState.value = UIState.Success(cachedFavoriteMovieList)
                    } ?: let {
                        _favoriteState.value = UIState.Error("В избранном пока ничего нет")
                    }
                } else {
                    _favoriteState.value = UIState.Success(cachedFavoriteMovieList)
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
                cachedFavoriteMovieList.add(movie)

                cachedMovieList =
                    cachedMovieList.filter { movie.movieId != it.movieId && movie.name != it.name }
                        .toMutableList()

                _homeState.value = UIState.Success(cachedMovieList)
                _favoriteState.value = UIState.Success(cachedFavoriteMovieList)
            }

            cachedMovieList =
                cachedMovieList.filter { movie.movieId != it.movieId && movie.name != it.name }
                    .toMutableList()
            _homeState.value = UIState.Success(cachedMovieList)
        }
    }


    fun deleteMovieFromFavorite(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMovieFromFavorites(movie)
            cachedFavoriteMovieList =
                cachedFavoriteMovieList.filter { movie.movieId != it.movieId && movie.name != it.name }
                    .toMutableList()
            if (!cachedMovieList.any { movie.movieId == it.movieId && movie.name == it.name }) {
                cachedMovieList.add(movie)
//                _homeState.value = UIState.Success(cachePopularMovieList)
            }
        }
    }


    fun deleteFromPopular(movie: Movie) {
        cachedMovieList =
            cachedMovieList.filter { movie.movieId != it.movieId && movie.name != it.name }
                .toMutableList()
    }

    fun plusPageCount() {
        pageCount += 1
    }

    private fun getRequestLanguage() {
        requestLanguageLifeData.value = repository.getRequestLanguageFromPreferences()
    }

    private fun getCategoryProperty() {
        categoryPropertyLifeData.value = repository.getDefaultCategoryFromPreferences()
    }

    fun putRequestLanguage(language: String) {
        repository.saveRequestLanguageToPreferences(language)
        getRequestLanguage()
    }

    fun putCategoryProperty(category: String) {
        repository.saveDefaultCategoryToPreferences(category)
        getCategoryProperty()
    }

    interface ApiCallback {
        fun onSuccess(films: MutableList<Movie>)
        fun onFailure(error: ErrorResponseDto)
    }

    companion object {
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val KEY_DEFAULT_LANGUAGE = "default_language"
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            KEY_DEFAULT_CATEGORY, KEY_DEFAULT_LANGUAGE -> {
                clearLoadedPages()
                clearCachedMovieList()
                getMoviesFromApi()
            }
        }
    }
}

