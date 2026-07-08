package com.balon.blmovie.feature.search

import com.balon.blmovie.core.model.MovieAdult
import com.balon.blmovie.core.model.PageState
import com.balon.blmovie.core.model.response.Movie

data class SearchUiState(

    /**
     * 页面UI状态
     */
    var realUiState: SearchPageState = SearchPageState.History,

    /**
     * 电影初始状态
     */
    val moviePageState: PageState = PageState.Loading,
    /**
     * 文章初始状态
     */
    val articlePageState: PageState = PageState.Loading,
    /**
     * 演员初始状态
     */
    val actorPageState: PageState = PageState.Loading,

    val movieList: List<Movie> = emptyList(),

    val movieAdultList:List<MovieAdult> = emptyList()

)

enum class SearchPageState {
    History,
    Suggest,
    Result
}