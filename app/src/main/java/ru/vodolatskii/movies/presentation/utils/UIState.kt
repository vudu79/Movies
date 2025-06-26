package ru.vodolatskii.movies.presentation.utils

import ru.vodolatskii.movies.domain.models.Movie


sealed class UIState() {
    data class Success(val listMovie: List<Movie> = emptyList()) : UIState()
    object Loading : UIState()
    data class Error(val message: String) :
        UIState()
}
//
//private val errorposterUrlsUrlList = listOf(
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/10900341/caf9f155-1a19-42f1-a0f3-9c8773e9083e/orig"),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/1599028/637271d5-61b4-4e46-ac83-6d07494c7645/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/1898899/5fb7d956-d5fb-4189-9ec9-1a051aaa7f41/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/1704946/e9008e2f-433f-43b0-b9b8-2ea8e3fb6c9b/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/6201401/90d57813-387c-44c4-81c1-ecddb3c417a5/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/1946459/5c758ac0-7a5c-4f00-a94f-1be680a312fb/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/1599028/4b27e219-a8a5-4d85-9874-57d6016e0837/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/10592371/ab964fb1-c8d1-4a78-8d49-c754d756d488/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/1777765/dd78edfd-6a1f-486c-9a86-6acbca940418/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/1777765/bb8afbd6-c9cd-4631-99e9-3fecf241dbaf/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/10900341/b3ed4aa7-c38c-4a35-a505-aaa6372ad9da/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/1704946/80eab631-346c-4c29-b14d-1fa1438158f9/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/6201401/022a58e3-5b9b-411b-bfb3-09fedb700401/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/4303601/617303b7-cfa7-4273-bd1d-63974bf68927/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/6201401/a7ef44b8-1983-4992-a889-da6f87a3f559/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/4303601/907e3552-9a1e-48d1-8a01-d013a76a8343/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/10592371/e2db42c4-4176-4a0f-b933-488412cd06a5/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/1599028/4adf61aa-3cb7-4381-9245-523971e5b4c8/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/4716873/85b585ea-410f-4d1c-aaa5-8d242756c2a4/orig")),
//    Movie(posterUrl =  "https://image.openmoviedb.com/kinopoisk-images/4774061/ccd69a69-7405-4b41-89a8-60dd6dc2a3ee/orig")),
//)

