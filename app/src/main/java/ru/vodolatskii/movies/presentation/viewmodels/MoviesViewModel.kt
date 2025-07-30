package ru.vodolatskii.movies.presentation.viewmodels

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ru.vodolatskii.movies.domain.MovieRepository
import ru.vodolatskii.movies.domain.models.Movie
import ru.vodolatskii.movies.presentation.utils.FavoriteUIState
import ru.vodolatskii.movies.presentation.utils.HomeUIState
import ru.vodolatskii.movies.presentation.utils.SingleLiveEvent
import ru.vodolatskii.movies.presentation.utils.SortEvents
import ru.vodolatskii.movies.presentation.utils.StorageSearchEvent
import ru.vodolatskii.movies.presentation.utils.UIStateStorage
import java.net.URL
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository,

    ) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val disposable = CompositeDisposable()
    val movieCountInDBLiveData: MutableLiveData<Int> = MutableLiveData()
    val allMoviesSavingLiveModeData: MutableLiveData<Boolean> = MutableLiveData()
    val ratingSavingModeLiveData: MutableLiveData<Int> = MutableLiveData()
    val dateSavingModeLiveData: MutableLiveData<Int> = MutableLiveData()
    val contentSourceLiveData: MutableLiveData<String> = MutableLiveData()
    val categoryPropertyLifeData: MutableLiveData<String> = MutableLiveData()
    val requestLanguageLifeData: MutableLiveData<String> = MutableLiveData()

    var messageSingleLiveEvent = SingleLiveEvent<String>()
        private set

    private val _isSearchViewVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSearchViewVisible: LiveData<Boolean> = _isSearchViewVisible


    var loadedPages: MutableSet<Int> = mutableSetOf()
        private set

    var pageNumber = 1
        private set

    private var _cachedMovieList: MutableSet<Movie> = mutableSetOf()
    private var _cachedFavoriteMovieList: MutableSet<Movie> = mutableSetOf()

    val homeUIState: BehaviorSubject<HomeUIState> = BehaviorSubject.create()
    val favoriteUIState: BehaviorSubject<FavoriteUIState> = BehaviorSubject.create()
    val storageUIState: BehaviorSubject<UIStateStorage> = BehaviorSubject.create()


    init {
        setupSettings()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }


    fun getMoviesFromApi() {
        homeUIState.onNext(HomeUIState.Loading)
        if (!loadedPages.contains(pageNumber) || _cachedMovieList.isEmpty()){
            disposable.add(
                repository.getMovieResponseFromKPApi(page = pageNumber)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess {
                        loadedPages.add(pageNumber)
                        messageSingleLiveEvent.postValue("Success loading data!")
                    }
                    .doOnError { error ->
                        messageSingleLiveEvent.postValue("Server error - ${error.message}")
                    }
                    .subscribe(
                        { movies ->
                            _cachedMovieList.addAll(movies)
                            homeUIState.onNext(HomeUIState.Success(_cachedMovieList.toList()))
                        },
                        { error ->
                            homeUIState.onNext(
                                HomeUIState.Error(
                                    error.message ?: "Unknown error"
                                )
                            )
                        }
                    )
            )
        }else{
            homeUIState.onNext(HomeUIState.Success(_cachedMovieList.toList()))
        }
    }


    fun getAllMoviesForStorageFragment() {
        disposable.add(
            repository.getAllMoviesFromDB()
                .subscribeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    storageUIState.onNext(UIStateStorage.Loading)
                }
                .subscribe(
                    { movies ->
                        if (movies.isNotEmpty()) {
                            storageUIState.onNext(UIStateStorage.Success(listMovie = movies))

                        } else {
                            storageUIState.onNext(UIStateStorage.Error("Content not found!"))
                        }
                    },
                    { error ->
                        storageUIState.onNext(UIStateStorage.Error("Unknown error $error"))
                    })
        )
    }

    fun onStorageSearchEvent(events: StorageSearchEvent) {
//            viewModelScope.launch(Dispatchers.IO) {
//                _storageState.value = UIStateStorage.Loading
//
//                val result: List<Movie>?
//                try {
//                    val rating = if (events.rating.equals("")) 0.0 else events.rating.toDouble()
//                    val date = if (events.date.equals("")) 0 else events.date.toInt()
//                    val title = events.title
//                    val genres = events.genres
//
//                    if (rating == 0.0 && date == 0 && title == "" && genres.isEmpty()) {
//                        result = cachedMovieList.value
//                        if (result.isNullOrEmpty()) {
//                            _storageState.value = UIStateStorage.Error("Content not found!")
//                        } else {
//                            _storageState.value = UIStateStorage.Success(listMovie = result)
//                        }
//
//                    } else {
//                        result = repository.getMoviesByFilter(
//                            rating = rating,
//                            date = date,
//                            title = title,
//                            genres = genres
//                        )
//
//                        if (result.isEmpty()) {
//                            _storageState.value = UIStateStorage.Error("Content not found!")
//                        } else {
//                            _storageState.value = UIStateStorage.Success(listMovie = result)
//                        }
//                    }
//
//                } catch (e: Exception) {
//                    _storageState.value = UIStateStorage.Error("Database read error - $e")
//                }
//            }
    }


    fun getFavoriteMovies() {
        disposable.add(
            repository.getAllMoviesFromFavorites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { movies ->
                        if (movies.isEmpty()) {
                            favoriteUIState.onNext(FavoriteUIState.Error("В избранном пока ничего нет"))
                        } else {
                            _cachedFavoriteMovieList.addAll(movies)
                            favoriteUIState.onNext(FavoriteUIState.Success(_cachedFavoriteMovieList.toList()))
                        }
                    },
                    { error ->
                        favoriteUIState.onNext(FavoriteUIState.Error("Unknown error $error"))
                    }
                )
        )
    }


    fun addMovieToFavorite(movie: Movie) {
        disposable.add(
            repository.getAllMoviesFromFavorites()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { movies ->
                        if (movies.isEmpty() || !movies.any { movie.apiId == it.apiId && movie.title == it.title }) {
                            Completable.fromSingle<Movie> {
                                repository.updateMovieToFavorite(true, movie.title)
                            }
                                .subscribeOn(Schedulers.io())
                                .subscribe()

//                            val m = movie.copy(isFavorite = true)
//
//                            _cachedFavoriteMovieList.add(m)
//                            favoriteUIState.onNext(FavoriteUIState.Success(_cachedFavoriteMovieList.toList()))
                            deleteFromCachedList(movie)
                        }
                        deleteFromCachedList(movie)
                    },
                    { error ->
                        favoriteUIState.onNext(FavoriteUIState.Error("Unknown error $error"))
                    }
                )
        )
    }


    fun deleteMovieFromFavorite(movie: Movie) {
        Completable.fromSingle<Movie> {
            repository.updateMovieToFavorite(false, movie.title)
        }
            .subscribeOn(Schedulers.io())
            .subscribe()

        _cachedFavoriteMovieList =
            _cachedFavoriteMovieList.filter { movie.apiId != it.apiId && movie.title != it.title }.toMutableSet()
        favoriteUIState.onNext(FavoriteUIState.Success(_cachedFavoriteMovieList.toList()))

        if (!_cachedMovieList.any { movie.apiId == it.apiId && movie.title == it.title }) {
            _cachedMovieList.add(movie)
            homeUIState.onNext(HomeUIState.Success(_cachedMovieList.toList()))
        }
    }


    fun deleteFromCachedList(movie: Movie) {
         _cachedMovieList =
            _cachedMovieList.filter { movie.apiId != it.apiId && movie.title != it.title }.toMutableSet()
        homeUIState.onNext(HomeUIState.Success(_cachedMovieList.toList()))
    }

    fun onSortRVEvents(event: SortEvents) {
//        when (event) {
//            SortEvents.ALPHABET -> {
//                val sortedList = cachedMovieList.value?.sortedBy {
//                    it.title
//                } ?: emptyList()
//                _computedUIState.value = UIState.Success(sortedList)
//            }
//
//            SortEvents.DATE -> {
//                val sortedList = cachedMovieList.value?.sortedBy {
//                    it.releaseDateTimeStump
//                } ?: emptyList()
//                _computedUIState.value = UIState.Success(sortedList)
//            }
//
//            SortEvents.RATING -> {
//                val sortedList = cachedMovieList.value?.sortedBy {
//                    it.rating
//                }?.reversed() ?: emptyList()
//                _computedUIState.value = UIState.Success(sortedList)
//            }

    }

    suspend fun loadWallpaper(url: String): Bitmap {
        return suspendCoroutine {
            val u = URL(url)
            val bitmap = BitmapFactory.decodeStream(u.openConnection().getInputStream())
            it.resume(bitmap)
        }
    }


    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?,
        key: String?
    ) {
        when (key) {
            KEY_DEFAULT_CATEGORY, KEY_DEFAULT_LANGUAGE -> {
                clearLoadedPages()
//                clearCachedMovieList()
                getMoviesFromApi()
            }
        }
    }

    fun deleteAllFromDB() {
        repository.deleteAllFromDB()
    }

    fun getMovieCountInDB() = repository.getMovieCount()


    fun clearLoadedPages() {
        loadedPages.clear()
    }

//    fun clearCachedMovieList() {
//        cachedMovieList.clear()
//    }

    fun switchSearchViewVisibility(state: Boolean) {
        _isSearchViewVisible.value = state
    }

    fun plusPageCount() {
//        if (!loadedPages.contains(pageCount)) {
        pageNumber += 1
//        }
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

    private fun registerSPListener() {
        repository.getPreference().registerOnSharedPreferenceChangeListener(this)
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

    private fun setupSettings() {
        registerSPListener()
        getContentSource()
        getAllMovieSavingMode()
        getRatingMovieSavingMode()
        getDateMovieSavingMode()
        getCategoryProperty()
        getRequestLanguage()
    }


    companion object {
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val KEY_DEFAULT_LANGUAGE = "default_language"
    }
}


