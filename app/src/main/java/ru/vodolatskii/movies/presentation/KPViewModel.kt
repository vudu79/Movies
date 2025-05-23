package ru.vodolatskii.movies.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.vodolatskii.movies.data.repository.Repository


class KPViewModel(
    private val repository: Repository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState

    init {
        viewModelScope.launch {
            repository.getMovieInfo()?.let {
                _uiState.value =
                    UIState.Success(listDoc = it.docs)
            } ?: let {
                _uiState.value = UIState.Error
            }
        }
    }
}

