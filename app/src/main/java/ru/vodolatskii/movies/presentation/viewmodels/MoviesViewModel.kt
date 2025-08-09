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
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.domain.MovieRepository
import ru.vodolatskii.movies.domain.models.Movie
import ru.vodolatskii.movies.presentation.utils.FavoriteUIState
import ru.vodolatskii.movies.presentation.utils.HomeUIState
import ru.vodolatskii.movies.presentation.utils.SingleLiveEvent
import ru.vodolatskii.movies.presentation.utils.SortEvents
import ru.vodolatskii.movies.presentation.utils.StorageSearchEvent
import ru.vodolatskii.movies.presentation.utils.UIStateStorage
import timber.log.Timber
import java.net.URL
import java.util.concurrent.TimeUnit
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

    private var currentPage = 1
    private var isLoading = false
    private var hasMore = true
    private var totalPages = 0
    private var totalItems = 0
    private var pageSize = App.instance.loadPopularMoviesLimit
    private var nextPageSize = 0

    private var currentPageSearch = 1
    private var isLoadingSearch = false
    private var hasMoreSearch = true
    private var totalPagesSearch = 0
    private var totalItemsSearch = 0
    private var pageSizeSearch = App.instance.loadPopularMoviesLimit
    private var nextPageSizeSearch = 0
    private var querySearch = ""

    private var cachedMovieList: MutableSet<Movie> = mutableSetOf()
    private var cachedMovieListSearch: MutableSet<Movie> = mutableSetOf()
    private var cachedFavoriteMovieList: MutableSet<Movie> = mutableSetOf()

    private val searchSubject: BehaviorSubject<String> = BehaviorSubject.createDefault("")
    val homeUIState: BehaviorSubject<HomeUIState> = BehaviorSubject.create()
    val favoriteUIState: BehaviorSubject<FavoriteUIState> = BehaviorSubject.create()
    val storageUIState: BehaviorSubject<UIStateStorage> = BehaviorSubject.create()

    init {
        setupSettings()
        disposable.add(
            searchSubject
                .debounce(1000, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isBlank()){
                        loadCurrentPage()
                    } else{
                        cachedMovieListSearch.clear()
                        currentPageSearch = 1
                        hasMoreSearch = true
                        totalPagesSearch = 0
                        totalItemsSearch = 0
                        nextPageSizeSearch = 0
                        loadNextPage(it)
                    }
                }
        )
    }

    fun loadNextPage(query: String) {
        if (query.isBlank()) {
            if (isLoading || !hasMore) return
            isLoading = true
            homeUIState.onNext(HomeUIState.Loading)
            disposable.add(
                repository.getMovieResponseFromKPApi(page = currentPage, query = query)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess {
                        messageSingleLiveEvent.postValue("Данные успешно получены!")
                    }
                    .doOnError { error ->
                        messageSingleLiveEvent.postValue("Ошибка на стороне сервера - ${error.message}")
                    }
                    .subscribe(
                        { response ->
                            isLoading = false
                            totalItems = response.total
                            totalPages = response.pages
                            pageSize = response.limit

                            if (response.movies.isNotEmpty()) {
                                cachedMovieList.addAll(response.movies)
                                currentPage++
                                hasMore = currentPage <= totalPages
                                nextPageSize = if (hasMore) {
                                    if (currentPage == totalPages) {
                                        totalItems - cachedMovieList.size
                                    } else {
                                        pageSize
                                    }
                                } else {
                                    0
                                }
                                homeUIState.onNext(
                                    HomeUIState.Success(
                                        cachedMovieList.toList(),
                                        hasMore,
                                        nextPageSize,
                                        currentPage - 1,
                                        totalPages,
                                        totalItems
                                    )
                                )
                            } else {
                                hasMore = false
                                homeUIState.onNext(
                                    HomeUIState.Success(
                                        cachedMovieList.toList(),
                                        false,
                                        0,
                                        currentPage - 1,
                                        totalPages,
                                        totalItems
                                    )
                                )
                            }
                        },
                        { error ->
                            isLoading = false
                            homeUIState.onNext(
                                HomeUIState.Error(
                                    error.message ?: "Неизвестная ошибка"
                                )
                            )
                        }
                    )
            )
        } else {

            if (query != querySearch) {
                querySearch = query
                cachedMovieListSearch.clear()
                currentPageSearch = 1
                isLoadingSearch = false
                hasMoreSearch = true
                totalPagesSearch = 0
                totalItemsSearch = 0
                pageSizeSearch = App.instance.loadPopularMoviesLimit
                nextPageSizeSearch = 0
            }

            if (isLoadingSearch || !hasMoreSearch) return

            isLoadingSearch = true
            homeUIState.onNext(HomeUIState.Loading)

            disposable.add(
                repository.getMovieResponseFromKPApi(page = currentPageSearch, query = query)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess {
                        messageSingleLiveEvent.postValue("Данные успешно получены!")
                    }
                    .doOnError { error ->
                        messageSingleLiveEvent.postValue("Ошибка на стороне сервера - ${error.message}")
                    }
                    .subscribe(
                        { response ->
                            isLoadingSearch = false
                            totalItemsSearch = response.total
                            totalPagesSearch = response.pages
                            pageSizeSearch = response.limit

                            if (response.movies.isNotEmpty()) {
                                cachedMovieListSearch.addAll(response.movies)
                                currentPageSearch++
                                hasMoreSearch = currentPageSearch <= totalPagesSearch
                                nextPageSizeSearch = if (hasMoreSearch) {
                                    if (currentPageSearch == totalPagesSearch) {
                                        totalItemsSearch - cachedMovieListSearch.size
                                    } else {
                                        pageSizeSearch
                                    }
                                } else {
                                    0
                                }
                                cachedMovieListSearch.forEach {
                                    Timber.d("list -- ${it.title}")
                                }
                                homeUIState.onNext(
                                    HomeUIState.Success(
                                        cachedMovieListSearch.toList(),
                                        hasMoreSearch,
                                        nextPageSizeSearch,
                                        currentPageSearch - 1,
                                        totalPagesSearch,
                                        totalItemsSearch
                                    )
                                )
                            } else {
                                hasMoreSearch = false
                                homeUIState.onNext(
                                    HomeUIState.Success(
                                        cachedMovieListSearch.toList(),
                                        false,
                                        0,
                                        currentPageSearch - 1,
                                        totalPagesSearch,
                                        totalItemsSearch
                                    )
                                )
                            }
                        },
                        { error ->
                            isLoadingSearch = false
                            homeUIState.onNext(
                                HomeUIState.Error(
                                    error.message ?: "Неизвестная ошибка"
                                )
                            )
                        }
                    )
            )
        }
    }

    fun loadCurrentPage() {
        if (cachedMovieList.isEmpty()) {
            loadNextPage("")
        } else {
            homeUIState.onNext(
                HomeUIState.Success(
                    cachedMovieList.toList(),
                    hasMore,
                    nextPageSize,
                    currentPage - 1,
                    totalPages,
                    totalItems,
                )
            )
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
                            storageUIState.onNext(UIStateStorage.Error("Контент не найден!"))
                        }
                    },
                    { error ->
                        storageUIState.onNext(UIStateStorage.Error("Неизвестная ошибка $error"))
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
                            cachedFavoriteMovieList.clear()
                            cachedFavoriteMovieList.addAll(movies)
                            favoriteUIState.onNext(FavoriteUIState.Success(cachedFavoriteMovieList.toList()))
                        }
                    },
                    { error ->
                        favoriteUIState.onNext(FavoriteUIState.Error("Неизвестная ошибка $error"))
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
                    { favoriteMovies ->
                        if (favoriteMovies.isEmpty() || !favoriteMovies.any { movie.apiId == it.apiId && movie.title == it.title }) {
                            Completable.fromSingle<Movie> {
                                repository.updateMovieToFavorite(true, movie.title)
                            }
                                .subscribeOn(Schedulers.io())
                                .subscribe()

//                            deleteFromCachedList(movie)
                        }
//                        deleteFromCachedList(movie)
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

        cachedFavoriteMovieList =
            cachedFavoriteMovieList.filter { movie.apiId != it.apiId && movie.title != it.title }
                .toMutableSet()
        favoriteUIState.onNext(FavoriteUIState.Success(cachedFavoriteMovieList.toList()))

        cachedMovieList =
            cachedMovieList.map {
                if (it.apiId == movie.apiId) it.isFavorite = false
                it
            }.toMutableSet()
        loadCurrentPage()
    }


    fun deleteFromCachedList(movie: Movie) {
        cachedMovieList =
            cachedMovieList.filter { movie.apiId != it.apiId && movie.title != it.title }
                .toMutableSet()
        homeUIState.onNext(
            HomeUIState.Success(
                movies = cachedMovieList.toList(),
                hasMore = hasMore,
                nextPageSize = nextPageSize,
                currentPage = currentPage,
                totalPages = totalPages,
                totalItems = totalItems
            )
        )
    }

    fun onSortRVEvents(event: SortEvents) {
        when (event) {
            SortEvents.ALPHABET -> {
                val sorted = cachedMovieList.sortedBy {
                    it.title
                }
                homeUIState.onNext(
                    HomeUIState.Success(
                        sorted, hasMore = hasMore,
                        nextPageSize = nextPageSize,
                        currentPage = currentPage,
                        totalPages = totalPages,
                        totalItems = totalItems
                    )
                )
            }

            SortEvents.DATE -> {
                val sorted = cachedMovieList.sortedBy {
                    it.releaseDateTimeStump
                }
                homeUIState.onNext(
                    HomeUIState.Success(
                        sorted,
                        hasMore = hasMore,
                        nextPageSize = nextPageSize,
                        currentPage = currentPage,
                        totalPages = totalPages,
                        totalItems = totalItems
                    )
                )
            }

            SortEvents.RATING -> {
                val sorted = cachedMovieList.sortedBy {
                    it.rating
                }.reversed()
                homeUIState.onNext(
                    HomeUIState.Success(
                        sorted,
                        hasMore = hasMore,
                        nextPageSize = nextPageSize,
                        currentPage = currentPage,
                        totalPages = totalPages,
                        totalItems = totalItems
                    )
                )
            }
        }
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
//                clearLoadedPages()
//                clearCachedMovieList()
                loadNextPage("")
            }
        }
    }

    fun deleteAllFromDB() {
        repository.deleteAllFromDB()
    }


//    fun clearLoadedPages() {
//        loadedPages.clear()
//    }

    fun switchSearchViewVisibility(state: Boolean) {
        _isSearchViewVisible.value = state
    }

//    fun plusPageCount() {
//        pageNumber += 1
//    }

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

    private fun setupSettings() {
        registerSPListener()
        getContentSource()
        getAllMovieSavingMode()
        getRatingMovieSavingMode()
        getDateMovieSavingMode()
        getCategoryProperty()
        getRequestLanguage()
    }


    fun onSearchViewQueryChanged(it: String) {
        searchSubject.onNext(it)
    }

    fun isLoading(): Boolean = isLoading
    fun hasMore(): Boolean = hasMore
    fun getCurrentPage(): Int = currentPage - 1
    fun getTotalPages(): Int = totalPages
    fun getTotalItems(): Int = totalItems

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    companion object {
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val KEY_DEFAULT_LANGUAGE = "default_language"
    }
}


