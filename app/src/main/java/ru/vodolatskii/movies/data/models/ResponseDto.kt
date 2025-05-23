package ru.vodolatskii.movies.data.models

class ResponseDto (
    val docs: List<Docs>,
    val total: Long,
    val limit: Long,
    val page: Long,
    val pages: Long,
)

data class Docs(
    val id: Long,
    val name: String,
    val alternativeName: String,
    val enName: String,
    val type: String,
    val year: Long,
    val description: String,
    val shortDescription: String,
    val movieLength: Long,
    val isSeries: Boolean,
    val ticketsOnSale: Boolean,
    val totalSeriesLength: Any?,
    val seriesLength: Any?,
    val ratingMpaa: String?,
    val ageRating: Long?,
    val top10: Any?,
    val top250: Any?,
    val typeNumber: Long,
    val status: Any?,
    val names: List<Name>,
//    val externalId: ExternalId,
    val logo: Logo,
    val poster: Poster,
    val backdrop: Backdrop,
    val rating: Rating,
    val votes: Votes,
    val genres: List<Genre>,
    val countries: List<Country>,
    val releaseYears: List<Any?>,
)

data class Name(
    val name: String,
    val language: String?,
    val type: String?,
)

//data class ExternalId(
//    @JsonProperty("kpHD")
//    val kpHd: String?,
//    val imdb: String,
//    val tmdb: Long,
//)

data class Logo(
    val url: String?,
    val previewUrl: String?,
)

data class Posters(
    val url: String?,
    val previewUrl: String?,
)

data class Backdrop(
    val url: String?,
    val previewUrl: String?,
)

data class Rating(
    val kp: Double,
    val imdb: Double,
    val filmCritics: Double,
    val russianFilmCritics: Double,
    val await: Any?,
)

data class Votes(
    val kp: Long,
    val imdb: Long,
    val filmCritics: Long,
    val russianFilmCritics: Long,
    val await: Long,
)

data class Genre(
    val name: String,
)

data class Country(
    val name: String,
)
