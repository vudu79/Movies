package ru.vodolatskii.movies.presentation.viewmodels

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.presentation.utils.SortEvents
import ru.vodolatskii.movies.data.entity.convertToModel
import ru.vodolatskii.movies.data.entity.dto.ErrorResponseDto
import ru.vodolatskii.movies.domain.MovieRepository
import ru.vodolatskii.movies.domain.models.Movie
import ru.vodolatskii.movies.presentation.utils.AndroidResourceProvider
import ru.vodolatskii.movies.presentation.utils.DataModel
import ru.vodolatskii.movies.presentation.utils.StorageSearchEvent
import ru.vodolatskii.movies.presentation.utils.UIState
import javax.inject.Inject


class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val resourceProvider: AndroidResourceProvider,

    ) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {

    val listViewDataModelModeData: MutableLiveData<List<DataModel>> = MutableLiveData()
    val movieCountInDBModeData: MutableLiveData<Int> = MutableLiveData()
    val allMoviesSavingLiveModeData: MutableLiveData<Boolean> = MutableLiveData()
    val ratingSavingModeLiveData: MutableLiveData<Int> = MutableLiveData()
    val dateSavingModeLiveData: MutableLiveData<Int> = MutableLiveData()
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
//        initListViewDataModel()
        getContentSource()
        getAllMovieSavingMode()
        getRatingMovieSavingMode()
        getDateMovieSavingMode()
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
                    repository.getMovieResponseFromTMDBApi(
                        page = pageCount,
                        callback = object : ApiCallback {
                            override fun onSuccess(films: MutableList<Movie>) {
                                cachedMovieList.addAll(films)
                                _homeState.value = UIState.Success(cachedMovieList)
                                loadedPages.add(pageCount)
                                films.forEach {
                                    repository.putMovieToDbWithSettings(it)
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
                    repository.getAllMoviesFromFavorites()?.let { movieWithGenreList ->
                        cachedFavoriteMovieList =
                            movieWithGenreList.map { it.convertToModel() }.toMutableList()

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

            if (fav.isNullOrEmpty() || !fav.any { movie.apiId == it.movie.apiId && movie.title == it.movie.title }) {
                repository.insertMovieToFavorites(movie)
                cachedFavoriteMovieList.add(movie)

                cachedMovieList =
                    cachedMovieList.filter { movie.apiId != it.apiId && movie.title != it.title }
                        .toMutableList()

                _homeState.value = UIState.Success(cachedMovieList)
                _favoriteState.value = UIState.Success(cachedFavoriteMovieList)
            }

            cachedMovieList =
                cachedMovieList.filter { movie.apiId != it.apiId && movie.title != it.title }
                    .toMutableList()
            _homeState.value = UIState.Success(cachedMovieList)
        }
    }


    fun deleteMovieFromFavorite(movie: Movie) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMovieFromFavorites(movie)
            cachedFavoriteMovieList =
                cachedFavoriteMovieList.filter { movie.apiId != it.apiId && movie.title != it.title }
                    .toMutableList()
            if (!cachedMovieList.any { movie.apiId == it.apiId && movie.title == it.title }) {
                cachedMovieList.add(movie)
//                _homeState.value = UIState.Success(cachePopularMovieList)
            }
        }
    }


    fun deleteFromCachedList(movie: Movie) {
        cachedMovieList =
            cachedMovieList.filter { movie.apiId != it.apiId && movie.title != it.title }
                .toMutableList()
    }

    fun deleteAllFromDB() {
        repository.deleteAllFromDB()
    }

    fun getMovieCountFromDB() {
        movieCountInDBModeData.value = repository.getMovieCount()
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

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            KEY_DEFAULT_CATEGORY, KEY_DEFAULT_LANGUAGE -> {
                clearLoadedPages()
                clearCachedMovieList()
                getMoviesFromApi()
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

    private fun getContentSource() {
        contentSourceLiveData.value = repository.getContentSourceFromPreferences()
    }

    fun putContentSource(source: String) {
        repository.saveContentSourceFromPreferences(source)
        getContentSource()
    }

    private fun getAllMovieSavingMode() {
        allMoviesSavingLiveModeData.value = repository.getMovieSavingMode()
    }

    fun setAllMovieSavingMode(isChecked: Boolean) {
        repository.saveMovieSavingMode(isChecked)
        getAllMovieSavingMode()
    }

    private fun getRatingMovieSavingMode() {
        ratingSavingModeLiveData.value = repository.getRatingMovieSavingMode()
    }

    fun setRatingMovieSavingMode(value: Int) {
        repository.saveRatingMovieSavingMode(value)
        getRatingMovieSavingMode()
    }

    private fun getDateMovieSavingMode() {
        dateSavingModeLiveData.value = repository.getDateMovieSavingMode()
    }

    fun setDateMovieSavingMode(value: Int) {
        repository.saveDateMovieSavingMode(value)
        getDateMovieSavingMode()
    }

    fun onStorageSearchEvent(events: StorageSearchEvent) {

        Log.d("mytag", "${events.title} --- ${events.rating}  --- ${events.date}")


        try {
            val rating = if (events.rating.equals("")) 0.0 else events.rating.toDouble()
            val date = if (events.date.equals("")) 0 else events.date.toInt()
            val title = events.title
            val genres = events.genres
            val result =
                repository.getAllFromDBByFilter(rating = rating, date = date, title = title)

            result.forEach {
                Log.d("mytag", "${it.title} --- ${it.rating}  --- ${it.releaseDateYear}")
            }

        } catch (e: Exception) {
            Log.d("mytag", "cast error $e")
        }
    }


//    private fun initListViewDataModel() {
//        val dataModel = ArrayList<DataModel>()
//        dataModel.add(DataModel(Pair(28, "Action"), false))
//        dataModel.add(DataModel(Pair(12, "Adventure"), false))
//        dataModel.add(DataModel(Pair(16, "Animation"), false))
//        dataModel.add(DataModel(Pair(35, "Comedy"), false))
//        dataModel.add(DataModel(Pair(80, "Crime"), false))
//        dataModel.add(DataModel(Pair(80, "Crime"), false))
//        dataModel.add(DataModel(Pair(99, "Documentary"), false))
//        dataModel.add(DataModel(Pair(18, "Drama"), false))
//        dataModel.add(DataModel(Pair(10751, "Family"), false))
//        dataModel.add(DataModel(Pair(14, "Fantasy"), false))
//        dataModel.add(DataModel(Pair(36, "History"), false))
//        dataModel.add(DataModel(Pair(27, "Horror"), false))
//        dataModel.add(DataModel(Pair(10402, "Music"), false))
//        dataModel.add(DataModel(Pair(9648, "Mystery"), false))
//        dataModel.add(DataModel(Pair(10749, "Romance"), false))
//        dataModel.add(DataModel(Pair(878, "Science Fiction"), false))
//        dataModel.add(DataModel(Pair(10770, "TV Movie"), false))
//        dataModel.add(DataModel(Pair(53, "Thriller"), false))
//        dataModel.add(DataModel(Pair(10752, "War"), false))
//        dataModel.add(DataModel(Pair(37, "Western"), false))
//        listViewDataModelModeData.value = dataModel
//    }
//

    interface ApiCallback {
        fun onSuccess(films: MutableList<Movie>)
        fun onFailure(error: ErrorResponseDto)
    }


    companion object {
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val KEY_DEFAULT_LANGUAGE = "default_language"
    }
}


