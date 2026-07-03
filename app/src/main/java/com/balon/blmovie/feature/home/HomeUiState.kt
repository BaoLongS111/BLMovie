package com.balon.blmovie.feature.home

import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.core.model.response.NetworkPageMeta

/**
 * 0: 下拉刷新空闲中
 * 1：下拉刷新中
 * 2：下拉刷新失败
 * 3：下拉刷新成功
 */
object RefreshState {
    const val IDLE = 0
    const val REFRESHING = 1
    const val ERROR = 2
    const val SUCCESS = 3
}

/**
 *  0：上拉加载空闲中
 *  1：上拉加载中
 *  2：上拉加载失败
 *  3：上拉加载成功
 *  4：没有更多数据
 */
object LoadMoreState {
    const val IDLE = 0
    const val LOADING = 1
    const val ERROR = 2
    const val SUCCESS = 3
    const val NO_MORE = 4
}

data class HomeUiState(

    /**
     * 轮播图
     */
    val banners: List<Movie> = emptyList(),

    /**
     * 首页电影列表
     */
    val movies: List<Movie> = emptyList(),

    /**
     * 第一次进入页面加载
     */
    val isFirstLoading: Boolean = false,

    /**
     * 下拉刷新状态
     */
    val refreshState: Int = RefreshState.IDLE,

    /**
     * 上拉加载更多状态
     */
    val loadMoreState: Int = LoadMoreState.IDLE,

    /**
     * 当前分页信息
     */
    val pageMeta: NetworkPageMeta? = null,

    /**
     * 错误信息
     */
    val errorMessage: String? = null



)