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
            preference.edit { putBoolean(KEY_FIRST_LAUNCH, false) }
        }
    }

    fun getInstance() = preference

    fun saveDefaultCategory(category: String) {
        preference.edit {
            putString(KEY_DEFAULT_CATEGORY, category)
        }
    }

    fun saveRequestLanguage(language: String) {
        preference.edit {
            putString(KEY_DEFAULT_LANGUAGE, language)
        }
    }

    fun getDefaultCategory() =
        preference.getString(KEY_DEFAULT_CATEGORY, DEFAULT_CATEGORY) ?: DEFAULT_CATEGORY

    fun getRequestLanguage() =
        preference.getString(KEY_DEFAULT_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE


    companion object {
        private const val SP_FILE_NAME = "settings"
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val DEFAULT_CATEGORY = "popular"

        private const val KEY_DEFAULT_LANGUAGE = "default_language"
        private const val DEFAULT_LANGUAGE = "ru-RU"
    }
}