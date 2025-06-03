package ru.vodolatskii.movies.data.repository.interfaces

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query
import ru.vodolatskii.movies.data.dto.ShortDocsResponseDto
import ru.vodolatskii.movies.data.dto.TMDBPopularMoviesRespDto

interface TMDBsApiService {
    @Headers(
        "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI3YzMyNDUyOTlhMDBiNGNmZTU5MzdhZGM5MDRkZGQwYiIsIm5iZiI6MTc0Nzk0NDU4MS4xMTIsInN1YiI6IjY4MmY4NDg1NjM2ODcwMmEyMWI2YTUxYiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.gqqroOOQUKINr-EuwLXLUVZpw-rj3VuzGeb08dtwjec",
        "accept: application/json"
    )
    @GET("trending/movie/{time_window}/")
    suspend fun getSearchResponse(
        @Path("time_window") timeWindow: String,
        @Query("language") language: String,
    ): Response<TMDBPopularMoviesRespDto>
}

