package ru.vodolatskii.movies.presentation

sealed class UIState() {
    data class Success(val title: String = "", val poster: String = "") : UIState()
    object Loading : UIState()
    object Error : UIState()
}