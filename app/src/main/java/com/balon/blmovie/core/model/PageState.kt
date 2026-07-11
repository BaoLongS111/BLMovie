package com.balon.blmovie.core.model

sealed interface PageState {

    data object Loading: PageState

    data object Success: PageState

    data class Error(
        val throwable: Throwable
    ): PageState

}

/**
 * 0: 下拉刷新空闲中
 * 1：下拉刷新中
 * 2：下拉刷新失败
 * 3：下拉刷新成功
 */
enum class RefreshState {
    IDLE,
    REFRESHING,
    ERROR,
    SUCCESS
}

/**
 *  0：上拉加载空闲中
 *  1：上拉加载中
 *  2：上拉加载失败
 *  3：上拉加载成功
 *  4：没有更多数据
 */
sealed interface LoadMoreState {

    data object Idle : LoadMoreState

    data object Loading : LoadMoreState

    data class Error(
        val throwable: Throwable
    ) : LoadMoreState

    data object Success : LoadMoreState

    data object NoMore : LoadMoreState
}