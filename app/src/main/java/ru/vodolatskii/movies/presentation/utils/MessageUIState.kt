package ru.vodolatskii.movies.presentation.utils

import ru.vodolatskii.movies.domain.models.Movie

sealed class MessageUIState (){
    data class Success(val message: String) : MessageUIState()
    data class Error(val message: String) : MessageUIState()
    object Init : MessageUIState()
}
