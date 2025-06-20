package ru.vodolatskii.movies.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

//class MyViewModelFactory(private val repository: MovieRepository) :
//    ViewModelProvider.NewInstanceFactory() {
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//
//        if (modelClass.isAssignableFrom(MoviesViewModel::class.java)) {
//            return MoviesViewModel(repository) as T
//        }
//
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}

class ViewModelFactory @Inject constructor(
myViewModelProvider: Provider<MoviesViewModel>
) : ViewModelProvider.Factory {
    private val providers = mapOf<Class<*>, Provider<out ViewModel>>(
        MoviesViewModel::class.java to myViewModelProvider
    )

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return providers[modelClass]!!.get() as T
    }
}
