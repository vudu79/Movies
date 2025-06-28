package ru.vodolatskii.movies.presentation.utils

import android.content.Context
import androidx.core.content.ContextCompat

class AndroidResourceProvider(private val context: Context) : ResourceProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }

    override fun getColor(resId: Int): Int {
        return ContextCompat.getColor(context, resId)
    }
}

interface ResourceProvider {
    fun getString(resId: Int): String
    fun getColor(resId: Int): Int
}