package ru.vodolatskii.remote_module.entity

data class FullDocsResponseDto(

    val docs: List<Doc>,
    val total: Long,
    val limit: Long,
    val page: Long,
    val pages: Long,
)

//
//data class Doc(
//    val id: Long,
//    val name: String,
//    val year: Long,
//    val description: String,
//    val rating: Rating,
//    val poster: Poster,
//    val genres: List<Genre>,
//    val persons: List<Person>,
//    val premiere: Premiere,
//    val enName: String,
//)
//
//data class Rating(
//    val kp: Double,
//    val imdb: Double,
//    val filmCritics: Long,
//    val russianFilmCritics: Long,
//    val await: Long?,
//)
//
//data class Poster(
//    val url: String,
//    val previewUrl: String,
//)
//
//data class Genre(
//    val name: String,
//)
//
//data class Person(
//    val id: Long,
//    val photo: String,
//    val name: String?,
//    val enName: String?,
//    val description: String?,
//    val profession: String,
//    val enProfession: String,
//)
//
//data class Premiere(
//    val country: Any?,
//    val russia: Any?,
//    val digital: Any?,
//    val cinema: Any?,
//    val bluray: Any?,
//    val dvd: Any?,
//    val world: String,
//)
//
