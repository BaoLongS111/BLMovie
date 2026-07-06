package com.balon.blmovie.feature.home

import com.balon.blmovie.core.model.Article
import com.balon.blmovie.core.model.Category
import com.balon.blmovie.core.model.HomeData
import com.balon.blmovie.core.model.LoadMoreState
import com.balon.blmovie.core.model.PageState
import com.balon.blmovie.core.model.RefreshState
import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.core.model.response.NetworkPageMeta

data class HomeUiState(

    /**
     * 轮播图
     */
    val banners: List<Movie> = emptyList(),

    /**
     * 首页数据聚合
     */
    val homeData: HomeData? = null,

    /**
     * 文章列表
     */
    val articleList: List<Article>? = emptyList(),

    /**
     * 整个页面状态
     */
    val pageState: PageState = PageState.Loading,

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