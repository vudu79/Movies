package ru.vodolatskii.movies.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.vodolatskii.movies.domain.MovieRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewModelFactory @Inject constructor(
    private val repository: MovieRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MoviesViewModel::class.java) -> {
                MoviesViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}



//
//class ViewModelFactory @Inject constructor(
//myViewModelProvider: Provider<MoviesViewModel>
//) : ViewModelProvider.Factory {
//    private val providers = mapOf<Class<*>, Provider<out ViewModel>>(
//        MoviesViewModel::class.java to myViewModelProvider
//    )
//
//    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return providers[modelClass]!!.get() as T
//    }
//}
