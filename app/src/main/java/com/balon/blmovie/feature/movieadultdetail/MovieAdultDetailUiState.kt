package com.balon.blmovie.feature.movieadultdetail

import com.balon.blmovie.core.model.LoadMoreState
import com.balon.blmovie.core.model.MovieAdultDetail
import com.balon.blmovie.core.model.PageState
import com.balon.blmovie.core.model.RefreshState
import com.balon.blmovie.core.model.response.NetworkPageMeta

data class MovieAdultDetailUiState(
    /**
     * 电影详情
     */
    val movieAdultDetail: MovieAdultDetail? = null,

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
    val loadMoreState: LoadMoreState = LoadMoreState.Idle,

    /**
     * 当前分页信息
     */
    val videoPageMeta: NetworkPageMeta? = null,

    /**
     * 当前分页信息
     */
    val commentPageMeta: NetworkPageMeta? = null,

)