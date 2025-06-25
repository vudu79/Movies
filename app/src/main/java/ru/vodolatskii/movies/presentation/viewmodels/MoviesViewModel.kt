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
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.common.SortEvents
import ru.vodolatskii.movies.data.entity.Movie
import ru.vodolatskii.movies.data.entity.dto.ErrorResponseDto
import ru.vodolatskii.movies.domain.MovieRepository
import ru.vodolatskii.movies.presentation.utils.AndroidResourceProvider
import ru.vodolatskii.movies.presentation.utils.UIState
import javax.inject.Inject


class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val resourceProvider: AndroidResourceProvider,

    ) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {

    val isAllMoviesSaveLiveData: MutableLiveData<Boolean> = MutableLiveData()
    val contentSourceLiveData: MutableLiveData<String> = MutableLiveData()
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
        setupSettings()
        repository.getPreference().registerOnSharedPreferenceChangeListener(this)
    }


    private fun setupSettings() {
        getSource()
        getMovieSavingMode()
        getCategoryProperty()
        getRequestLanguage()
    }

    fun getMoviesFromStorage(): List<Movie> {
        val moviesFromStorage = repository.getAllFromDB()
        cachedMovieList.clear()
        cachedMovieList.addAll(moviesFromStorage)
        return moviesFromStorage
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
                                films.forEach {
                                    repository.putToDb(it)
                                }
                            }
                            override fun onFailure(error: ErrorResponseDto) {
                                if (getMoviesFromStorage().isEmpty()) {
                                    _homeState.value =
                                        UIState.Error(resourceProvider.getString(R.string.request_error))
                                } else {
                                    _homeState.value = UIState.Success(cachedMovieList)
                                }
                            }
                        })
                } else {
                    _homeState.value = UIState.Success(cachedMovieList)
                }
            } catch (e: Exception) {
                if (getMoviesFromStorage().isEmpty()) {
                    _homeState.value =
                        UIState.Error(resourceProvider.getString(R.string.request_error))
                } else {
                    _homeState.value = UIState.Success(cachedMovieList)
                }
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

            if (fav.isNullOrEmpty() || !fav.any { movie.movieId == it.movieId && movie.title == it.title }) {
                repository.insertMovieToFavorites(movie)
                cachedFavoriteMovieList.add(movie)

                cachedMovieList =
                    cachedMovieList.filter { movie.movieId != it.movieId && movie.title != it.title }
                        .toMutableList()

                _homeState.value = UIState.Success(cachedMovieList)
                _favoriteState.value = UIState.Success(cachedFavoriteMovieList)
            }

            cachedMovieList =
                cachedMovieList.filter { movie.movieId != it.movieId && movie.title != it.title }
                    .toMutableList()
            _homeState.value = UIState.Success(cachedMovieList)
        }
    }


    fun deleteMovieFromFavorite(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMovieFromFavorites(movie)
            cachedFavoriteMovieList =
                cachedFavoriteMovieList.filter { movie.movieId != it.movieId && movie.title != it.title }
                    .toMutableList()
            if (!cachedMovieList.any { movie.movieId == it.movieId && movie.title == it.title }) {
                cachedMovieList.add(movie)
//                _homeState.value = UIState.Success(cachePopularMovieList)
            }
        }
    }


    fun deleteFromPopular(movie: Movie) {
        cachedMovieList =
            cachedMovieList.filter { movie.movieId != it.movieId && movie.title != it.title }
                .toMutableList()
    }


    fun onSortRVEvents(event: SortEvents) {
        when (event) {
            SortEvents.ALPHABET -> {
                val sortedList = cachedMovieList.sortedBy {
                    it.title
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

    fun plusPageCount() {
        pageCount += 1
    }

    private fun getRequestLanguage() {
        requestLanguageLifeData.value = repository.getRequestLanguageFromPreferences()
    }

    fun putRequestLanguage(language: String) {
        repository.saveRequestLanguageToPreferences(language)
        getRequestLanguage()
    }

    private fun getCategoryProperty() {
        categoryPropertyLifeData.value = repository.getDefaultCategoryFromPreferences()
    }

    fun putCategoryProperty(category: String) {
        repository.saveDefaultCategoryToPreferences(category)
        getCategoryProperty()
    }

    interface ApiCallback {
        fun onSuccess(films: MutableList<Movie>)
        fun onFailure(error: ErrorResponseDto)
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

    private fun getSource() {
        contentSourceLiveData.value = repository.getContentSourceFromPreferences()
    }

    fun putSource(source: String) {
        repository.saveContentSourceFromPreferences(source)
        getSource()
    }

    private fun getMovieSavingMode() {
        isAllMoviesSaveLiveData.value = repository.getMovieSavingMode()
    }

    fun setMovieSavingMode(isChecked: Boolean) {
        repository.saveMovieSavingMode(isChecked)
        getMovieSavingMode()
    }


    companion object {
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val KEY_DEFAULT_LANGUAGE = "default_language"
    }
}


