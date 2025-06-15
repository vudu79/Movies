package ru.vodolatskii.movies.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.vodolatskii.movies.domain.Repository

class MyViewModelFactory(private val repository: Repository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(MoviesViewModel::class.java)) {
            return MoviesViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

