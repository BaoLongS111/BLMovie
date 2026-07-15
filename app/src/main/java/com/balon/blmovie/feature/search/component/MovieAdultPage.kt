package com.balon.blmovie.feature.search.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.balon.blmovie.core.design.component.MyLoadingBar
import com.balon.blmovie.core.design.theme.SpaceOuterHeight
import com.balon.blmovie.core.extension.shouldLoadMore
import com.balon.blmovie.core.model.LoadMoreState
import com.balon.blmovie.core.model.MovieAdult
import com.balon.blmovie.core.model.response.NetworkPageMeta
import com.balon.blmovie.feature.search.SearchUiState
import com.quick.app.core.extension.clickableNoRipple

private const val MOVIE_GRID_PREFETCH_ITEM_COUNT = 6
@Composable
fun MovieAdultPage(
    uiState: SearchUiState,
    keyword: String,
    pagination: NetworkPageMeta,
    list: List<MovieAdult>,
    modifier: Modifier = Modifier,
    onMovieAdultItemClick: (MovieAdult) -> Unit,
    onLoadMore: () -> Unit,
) {
    val gridState = rememberLazyGridState()

    /*
     * 当前列表数量达到接口 total 时，
     * 即使 ViewModel 还没有来得及写入 NoMore，
     * 页面也能马上显示“我是有底线的”。
     */
    val canLoadMore =
        list.size < pagination.total!!

    val footerState: LoadMoreState =
        if (
            list.isNotEmpty() &&
            !canLoadMore
        ) {
            LoadMoreState.NoMore
        } else {
            uiState.movieLoadMoreState
        }

    val shouldLoadMore by remember(
        gridState,
        list.size
    ) {
        derivedStateOf {
            gridState.shouldLoadMore(
                dataItemCount = list.size,
                prefetchItemCount =
                    MOVIE_GRID_PREFETCH_ITEM_COUNT
            )
        }
    }

    /*
     * 提前加载下一页。
     *
     * Loading、Error 和 NoMore 状态下都不会重复触发。
     * Success 后如果仍然接近底部，会继续判断是否需要下一页。
     */
    LaunchedEffect(
        shouldLoadMore,
        canLoadMore,
        uiState.movieLoadMoreState,
        list.size
    ) {
        val state = uiState.movieLoadMoreState

        if (
            shouldLoadMore &&
            canLoadMore &&
            state !is LoadMoreState.Loading &&
            state !is LoadMoreState.Error &&
            state !is LoadMoreState.NoMore
        ) {
            onLoadMore()
        }
    }

    val navigationBarPadding = WindowInsets.navigationBars
        .asPaddingValues()
        .calculateBottomPadding()


    Column(
        modifier = modifier.fillMaxSize()
    ) {
        SearchResultHeader(
            keyword = keyword,
            pagination = pagination
        )
        SpaceOuterHeight()
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                // 系统底部手势区域 + 额外安全距离
                bottom = navigationBarPadding + 12.dp
            ),
            horizontalArrangement =
                Arrangement.spacedBy(10.dp),
            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = list,
                key = { movie ->
                    movie.vodId
                }
            ) { movie ->
                SearchResultMovieAdultItem(
                    item = movie,
                    onMovieAdultItemClick =
                        onMovieAdultItemClick
                )
            }

            /*
             * 加载状态始终放在网格最后，并占满三列。
             */
            item(
                key = "movie_adult_load_more_footer",
                span = {
                    GridItemSpan(maxLineSpan)
                }
            ) {
                MovieLoadMoreFooter(
                    state = footerState,
                    onRetryClick = {}
                )
            }
        }
    }
}

@Composable
private fun MovieLoadMoreFooter(
    state: LoadMoreState,
    onRetryClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // Footer 始终保留固定高度，状态切换时列表不会跳动
            .height(36.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = state,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(
                        durationMillis = 220
                    )
                ) togetherWith fadeOut(
                    animationSpec = tween(
                        durationMillis = 120
                    )
                )
            },
            label = "movieLoadMoreFooter"
        ) { targetState ->
            when (targetState) {
                is LoadMoreState.Error -> Text(
                    text = "- ${targetState.throwable.message?:"未知错误"},点击重试 -",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.clickableNoRipple{
                        onRetryClick()
                    }
                )

                LoadMoreState.Loading -> MyLoadingBar(message = "加载中...")

                LoadMoreState.NoMore -> {
                    Text(
                        text = "- 我是有底线的 -",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                }
                LoadMoreState.Idle,
                LoadMoreState.Success -> Unit
            }
        }
    }
}