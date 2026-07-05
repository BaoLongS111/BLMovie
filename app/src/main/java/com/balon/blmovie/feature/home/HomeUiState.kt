package com.balon.blmovie.feature.home

import com.balon.blmovie.core.model.LoadMoreState
import com.balon.blmovie.core.model.PageState
import com.balon.blmovie.core.model.RefreshState
import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.core.model.response.NetworkPageMeta

data class HomeUiState(

    /**
     * 整个页面状态
     */
    val pageState: PageState = PageState.Loading,

    /**
     * 轮播图
     */
    val banners: List<Movie> = emptyList(),

    /**
     * 首页电影列表
     */
    val movies: List<Movie> = emptyList(),

    /**
     * 下拉刷新状态
     */
    val refreshState: RefreshState = RefreshState.IDLE,

    /**
     * 上拉加载更多状态
     */
    val loadMoreState: LoadMoreState = LoadMoreState.IDLE,

    /**
     * 当前分页信息
     */
    val pageMeta: NetworkPageMeta? = null,

)