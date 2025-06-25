package ru.vodolatskii.movies.data.sharedPref

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferenceProvider(context: Context) {
    private val appContext = context.applicationContext
    private val preference: SharedPreferences =
        appContext.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE)

    init {
        if (preference.getBoolean(KEY_FIRST_LAUNCH, true)) {
            preference.edit { putString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) }
            preference.edit { putString(KEY_DEFAULT_LANGUAGE, DEFAULT_LANGUAGE) }
            preference.edit { putString(KEY_CONTENT_SOURCE, DEFAULT_CONTENT_SOURCE) }
            preference.edit { putBoolean(KEY_SAVE_MOVIE_MODE, DEFAULT_SAVE_MOVIE_MODE) }
            preference.edit { putInt(KEY_RATING_SAVE_MOVIE_MODE, DEFAULT_RATING_SAVE_MOVIE_MODE) }
            preference.edit { putInt(KEY_DATE_SAVE_MOVIE_MODE, DEFAULT_DATE_SAVE_MOVIE_MODE) }

            preference.edit { putBoolean(KEY_FIRST_LAUNCH, false) }
        }
    }

    fun getInstance() = preference

    fun getDefaultCategory() =
        preference.getString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) ?: DEFAULT_CATEGORY
    fun saveDefaultCategory(category: String) {
        preference.edit {
            putString(KEY_DEFAULT_CATEGORY, category)
        }
    }

    fun getRequestLanguage() =
        preference.getString(KEY_DEFAULT_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
    fun saveRequestLanguage(language: String) {
        preference.edit {
            putString(KEY_DEFAULT_LANGUAGE, language)
        }
    }

    fun getContentSource() =
        preference.getString(KEY_CONTENT_SOURCE, DEFAULT_CONTENT_SOURCE) ?: DEFAULT_CONTENT_SOURCE
    fun saveContentSource(source: String) {
        preference.edit {
            putString(KEY_CONTENT_SOURCE, source)
        }
    }

    fun getMovieSavingMode() =
        preference.getBoolean(KEY_SAVE_MOVIE_MODE, DEFAULT_SAVE_MOVIE_MODE)
    fun saveMovieSavingMode(checked: Boolean) {
        preference.edit {
            putBoolean(KEY_SAVE_MOVIE_MODE, checked)
        }
    }

    fun getRatingMovieSavingMode()=
        preference.getInt(KEY_RATING_SAVE_MOVIE_MODE, DEFAULT_RATING_SAVE_MOVIE_MODE)
    fun saveRatingMovieSavingMode(value: Int) {
        preference.edit {
            putInt(KEY_RATING_SAVE_MOVIE_MODE, value)
        }
    }

    fun getDateMovieSavingMode()=
        preference.getInt(KEY_DATE_SAVE_MOVIE_MODE, DEFAULT_DATE_SAVE_MOVIE_MODE)
    fun saveDateMovieSavingMode(value: Int) {
        preference.edit {
            putInt(KEY_DATE_SAVE_MOVIE_MODE, value)
        }
    }


    companion object {
        private const val SP_FILE_NAME = "settings"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val DEFAULT_CATEGORY = "popular"

        private const val KEY_DEFAULT_LANGUAGE = "default_language"
        private const val DEFAULT_LANGUAGE = "ru-RU"

        private const val KEY_CONTENT_SOURCE = "default_source"
        private const val DEFAULT_CONTENT_SOURCE = "internet"

        private const val KEY_SAVE_MOVIE_MODE = "save_mode"
        private const val DEFAULT_SAVE_MOVIE_MODE = true

        private const val KEY_RATING_SAVE_MOVIE_MODE = "rating_save_mode"
        private const val DEFAULT_RATING_SAVE_MOVIE_MODE = 0

        private const val KEY_DATE_SAVE_MOVIE_MODE = "date_save_mode"
        private const val DEFAULT_DATE_SAVE_MOVIE_MODE = 1950
    }
}