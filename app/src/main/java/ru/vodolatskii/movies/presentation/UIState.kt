package ru.vodolatskii.movies.presentation

import ru.vodolatskii.movies.data.models.Doc

sealed class UIState() {
    data class Success(val listDoc: List<Doc> = emptyList()) : UIState()
    object Loading : UIState()
    data class Error(val message: String ) : UIState()
}