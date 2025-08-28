package ru.vodolatskii.movies.presentation.viewmodels

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ru.vodolatskii.movies.App
import ru.vodolatskii.movies.common.ThemeManager
import ru.vodolatskii.movies.domain.MovieRepository
import ru.vodolatskii.movies.domain.models.Movie
import ru.vodolatskii.movies.presentation.utils.SimpleUIState
import ru.vodolatskii.movies.presentation.utils.HomeUIState
import ru.vodolatskii.movies.presentation.utils.SingleLiveEvent
import ru.vodolatskii.movies.presentation.utils.SortEvents
import ru.vodolatskii.movies.presentation.utils.StorageSearchEvent
import ru.vodolatskii.movies.presentation.utils.UIStateStorage
import ru.vodolatskii.remote_module.entity.SunSetDto
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository,

    ) : ViewModel(), SharedPreferences.OnSharedPreferenceChangeListener {
    private val disposable = CompositeDisposable()

    private val systemThemeLifeData: MutableLiveData<Boolean> = MutableLiveData(true)
    val dayNightThemeLifeData: MutableLiveData<String> = MutableLiveData(SYSTEM_THEME)
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
    val favoriteUIState: BehaviorSubject<SimpleUIState> = BehaviorSubject.create()
    val reminderUIState: BehaviorSubject<SimpleUIState> = BehaviorSubject.create()
    val storageUIState: BehaviorSubject<UIStateStorage> = BehaviorSubject.create()
    val locationSubject: BehaviorSubject<Location> = BehaviorSubject.create()

    init {
        setupSettings()

        disposable.add(
            searchSubject
                .debounce(1000, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (it.isBlank()) {
                        loadCurrentPage()
                    } else {
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

        disposable.add(
            locationSubject
                .debounce(1000, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    changeThemeBySunSet(it)
                }
        )
    }

    fun updateLocation(location: Location) {
        locationSubject.onNext(location)
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
                            favoriteUIState.onNext(SimpleUIState.Error("В избранном пока ничего нет"))
                        } else {
                            cachedFavoriteMovieList.clear()
                            cachedFavoriteMovieList.addAll(movies)
                            favoriteUIState.onNext(SimpleUIState.Success(cachedFavoriteMovieList.toList()))
                        }
                    },
                    { error ->
                        favoriteUIState.onNext(SimpleUIState.Error("Неизвестная ошибка $error"))
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

                        }
                    },
                    { error ->
                        favoriteUIState.onNext(SimpleUIState.Error("Unknown error $error"))
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
        favoriteUIState.onNext(SimpleUIState.Success(cachedFavoriteMovieList.toList()))

        cachedMovieList =
            cachedMovieList.map {
                if (it.apiId == movie.apiId) it.isFavorite = false
                it
            }.toMutableSet()
        loadCurrentPage()
    }


    fun updateReminderForMovie(movieId: Long, isReminder: Boolean, millis: Long, str: String) {
        Completable.fromSingle<Movie> {
            repository.updateReminderForMovie(
                movieId,
                isReminder,
                millis,
                str
            )
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun getReminderMovies() {
        disposable.add(
            repository.getRemindedMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { movies ->
                        if (movies.isEmpty()) {
                            reminderUIState.onNext(SimpleUIState.Error("Напоминаний нет"))
                        } else {
                            reminderUIState.onNext(SimpleUIState.Success(movies))
                        }
                    },
                    { error ->
                        reminderUIState.onNext(SimpleUIState.Error("Неизвестная ошибка $error"))
                    }
                )
        )
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
                loadNextPage("")
            }
        }
    }

    private fun changeThemeBySunSet(location: Location) {
        disposable.add(
            repository.getSunDataFromApi(location.latitude, location.longitude, getDate())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess {
                    messageSingleLiveEvent.postValue("Данные успешно получены!")
                }
                .doOnError { error ->
                    messageSingleLiveEvent.postValue("Ошибка на стороне сервера - ${error.message}")
                }
                .subscribe { it ->
                    val currTheme = ThemeManager.getCurrentTheme()
                    if (isSystemThemeActive()) {
                        if (isDayNaw(it)) {
                            if (currTheme == AppCompatDelegate.MODE_NIGHT_YES) ThemeManager.setTheme(
                                DAY_THEME
                            )
                        } else {
                            if (currTheme == AppCompatDelegate.MODE_NIGHT_NO) ThemeManager.setTheme(
                                NIGHT_THEME
                            )
                        }
                    }
                }
        )
    }

    private fun isDayNaw(dto: SunSetDto): Boolean {
        val nowTime = LocalTime.now()
        val sunriseTime = dto.results.sunrise.utcTimeToLocal()
        return sunriseTime != null && nowTime.isAfter(sunriseTime)
    }


    fun deleteAllFromDB() {
        repository.deleteAllFromDB()
    }


    fun switchSearchViewVisibility(state: Boolean) {
        _isSearchViewVisible.value = state
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


    private fun getDayNightThemeProperty(): String {
        dayNightThemeLifeData.value = repository.getThemeFromPreferences()
        return dayNightThemeLifeData.value ?: ""
    }

    fun putDayNightThemeProperty(theme: String) {
        repository.saveThemeToPreferences(theme)
        getDayNightThemeProperty()
        ThemeManager.setTheme(theme)
    }

    fun isSystemThemeActive(): Boolean {
        systemThemeLifeData.value = repository.getSystemThemeFromPreferences()
        return systemThemeLifeData.value ?: false
    }

    fun putSystemThemeProperty(theme: Boolean) {
        repository.saveSystemThemeToPreferences(theme)
        isSystemThemeActive()
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
        getDayNightThemeProperty()
    }


    fun onSearchViewQueryChanged(it: String) {
        searchSubject.onNext(it)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun handleBroadCastIntent(intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_POWER_CONNECTED -> {
                if (isSystemThemeActive()) {
                    putDayNightThemeProperty(DAY_THEME)
                    ThemeManager.setTheme(getDayNightThemeProperty())
                }
            }

            Intent.ACTION_BATTERY_LOW -> {
                if (isSystemThemeActive()) {
                    putDayNightThemeProperty(NIGHT_THEME)
                    ThemeManager.setTheme(getDayNightThemeProperty())
                }
            }

            Intent.ACTION_BATTERY_OKAY -> {
                if (isSystemThemeActive()) {
                    putDayNightThemeProperty(DAY_THEME)
                    ThemeManager.setTheme(getDayNightThemeProperty())
                }
            }
        }
    }

    private fun getDate() =
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()))

    fun String.utcTimeToLocal(): LocalTime? {
        return try {
            val utcTime = LocalTime.parse(this, DateTimeFormatter.ofPattern("h:mm:ss a", Locale.US))
            val utcDateTime = ZonedDateTime.now(ZoneId.of("UTC"))
                .withHour(utcTime.hour)
                .withMinute(utcTime.minute)
                .withSecond(utcTime.second)

            val localDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault())
            localDateTime.toLocalTime()
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val KEY_DEFAULT_LANGUAGE = "default_language"
        private const val DAY_THEME = "day"
        private const val NIGHT_THEME = "night"
        private const val SYSTEM_THEME = "system"

    }
}


