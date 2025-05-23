package ru.vodolatskii.movies.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.vodolatskii.movies.data.repository.Repository

class MyViewModelFactory(private val repository: Repository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(KPViewModel::class.java)) {
            return KPViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

