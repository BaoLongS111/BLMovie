package com.balon.blmovie.feature.search

import com.balon.blmovie.core.model.LoadMoreState
import com.balon.blmovie.core.model.MovieAdult
import com.balon.blmovie.core.model.PageState
import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.core.model.response.NetworkPageMeta

data class SearchUiState(
    // 搜索历史
    val searchHistory: List<String> = emptyList(),
    // 搜索建议
    val searchSuggest: List<String> = emptyList(),

    var keywords: String,

    /**
     * 页面UI状态
     */
    var realUiState: SearchPageState = SearchPageState.History,


    val movieList: List<Movie> = emptyList(),
    val moviePagination: NetworkPageMeta? = null,

    /**
     * 电影状态
     */
    val moviePageState: PageState = PageState.Loading,
    /**
     * 电影上拉加载更多状态
     */
    var movieLoadMoreState: LoadMoreState = LoadMoreState.Idle,

    /**
     * 文章状态
     */
    val articlePageState: PageState = PageState.Loading,
    /**
     * 文章上拉加载更多状态
     */
    val articleLoadMoreState: LoadMoreState = LoadMoreState.Idle,

    /**
     * 演员状态
     */
    val actorPageState: PageState = PageState.Loading,
    /**
     * 演员上拉加载更多状态
     */
    val actorLoadMoreState: LoadMoreState = LoadMoreState.Idle,

    val movieAdultList: List<MovieAdult> = emptyList(),
    val movieAdultPagination: NetworkPageMeta? = null,
    /**
     * 电影Adult状态
     */
    val movieAdultPageState: PageState = PageState.Loading,
    /**
     * 电影Adult上拉加载更多状态
     */
    var movieAdultLoadMoreState: LoadMoreState = LoadMoreState.Idle,


    )

enum class SearchPageState {
    History,
    Suggest,
    Result
}