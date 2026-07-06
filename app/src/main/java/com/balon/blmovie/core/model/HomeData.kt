package com.balon.blmovie.core.model

import com.balon.blmovie.core.model.response.Movie
import kotlinx.serialization.Serializable

@Serializable
data class HomeData(
    val actionMovie: List<Movie>,
    val actor: List<Actor>,
    val category: List<Category>,
    val chinaFun: List<Movie>,
    val chinaMovie: List<Movie>,
    val japanKoreanMovie: List<Movie>
) {
}