package com.balon.blmovie.feature.moviedetail

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.balon.blmovie.core.design.component.MyErrorScreen
import com.balon.blmovie.core.design.component.MyVideoPlayer
import com.balon.blmovie.core.design.theme.BLMovieTheme
import com.balon.blmovie.core.design.theme.SpaceOuterHeight
import com.balon.blmovie.core.extension.parsePlayEpisodes
import com.balon.blmovie.core.model.PageState
import com.balon.blmovie.core.model.PlayEpisode
import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.feature.moviedetail.component.DetailFunctionSection
import com.balon.blmovie.feature.moviedetail.component.EpisodeBottomSheet
import com.balon.blmovie.feature.moviedetail.component.EpisodeSection
import com.balon.blmovie.feature.moviedetail.component.MovieDescriptionSection
import com.balon.blmovie.feature.moviedetail.component.MovieDetailShimmer
import com.balon.blmovie.feature.moviedetail.component.MovieDetailSkeletonScreen
import com.balon.blmovie.feature.moviedetail.component.MovieDetailTabRow
import com.balon.blmovie.feature.moviedetail.component.MovieInfoHeader
import com.balon.blmovie.feature.moviedetail.component.PlayerFullScreenEffect
import com.balon.blmovie.feature.moviedetail.navigation.MovieDetailRoutes
import kotlinx.coroutines.launch

@Composable
fun MovieDetailRoute(
    navKey: MovieDetailRoutes.MovieDetail,
    animatedContentScope: AnimatedContentScope? = null,
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
) {

    val viewModel = hiltViewModel<MovieDetailViewModel, MovieDetailViewModel.Factory>(
        creationCallback = { factory ->
            factory.create(navKey)
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pageState = uiState.pageState

    /*
    * 当前路由是否还在执行进入或退出动画。
    */
    val isRouteAnimating =
        animatedContentScope?.transition?.isRunning == true

    /*
     * 实际显示在界面上的状态。
     *
     * 初始固定显示骨架屏，防止数据加载太快，
     * 导致进入页面的途中直接显示 Success。
     */
    var displayPageState by remember {
        mutableStateOf<PageState>(PageState.Loading)
    }

    /*
 * 真实数据状态发生变化，或者路由动画状态发生变化时执行。
 */
    LaunchedEffect(
        pageState,
        isRouteAnimating
    ) {
        when {
            /*
             * 进入 Loading 时立即显示骨架屏。
             * 主要用于首次加载和错误重试。
             */
            pageState is PageState.Loading -> {
                displayPageState = PageState.Loading
            }

            /*
             * Success 或 Error 只有在路由动画结束后才显示。
             */
            !isRouteAnimating -> {
                displayPageState = pageState
            }

            /*
             * 路由动画还在执行时不处理，
             * 继续保持当前的骨架屏。
             */
            else -> Unit
        }
    }


    AnimatedContent(
        targetState = displayPageState,
        modifier = Modifier.fillMaxSize(),
        contentKey = { it::class },
        transitionSpec = {
            if (targetState is PageState.Error) {
                (
                        fadeIn(
                            tween(
                                durationMillis = 280,
                                delayMillis = 40,
                                easing = FastOutSlowInEasing
                            )
                        ) + slideInVertically(
                            animationSpec = tween(
                                durationMillis = 320,
                                easing = FastOutSlowInEasing
                            ),
                            initialOffsetY = { it / 18 }
                        )
                        ).togetherWith(
                        fadeOut(
                            tween(durationMillis = 150)
                        )
                    )
            } else {
                fadeIn(
                    tween(durationMillis = 180)
                ).togetherWith(
                    fadeOut(
                        tween(durationMillis = 120)
                    )
                )
            }
        },
        label = "movieDetailPageState"
    ) { currentPageState ->
        when (currentPageState) {
            is PageState.Loading -> {
                MovieDetailShimmer()
            }

            is PageState.Success -> {
                MovieDetailScreen(
                    uiState = uiState,
                    onBackClick = onBack,
                    onHomeClick = onHomeClick
                )
            }

            is PageState.Error -> {
                MyErrorScreen(
                    throwable = currentPageState.throwable,
                    onRetryClick = viewModel::onRetry
                )
            }
        }
    }

}

@Composable
fun MovieDetailScreen(
    uiState: MovieDetailUiState,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val movie = uiState.movieDetail ?: return
    val episodes: List<PlayEpisode> = remember(movie.playUrl) {
        movie.playUrl.parsePlayEpisodes()
    }

    var isLeavingPage by remember {
        mutableStateOf(false)
    }

    /**
     * 点全屏
     * targetFullScreen = true
     * 当前还是竖屏，所以 currentFullScreen = false
     * 控制栏隐藏
     * 等屏幕横过来
     * currentFullScreen = true
     * 显示全屏 UI
     */

    /**
     * 点退出全屏
     * targetFullScreen = false
     * 当前还是横屏，所以 currentFullScreen = true
     * 控制栏隐藏
     * 等屏幕竖回来
     * currentFullScreen = false
     * 显示普通 UI
     */

    var currentEpisode: PlayEpisode? by remember(episodes) {
        mutableStateOf(episodes.firstOrNull())
    }

    var targetFullScreen by rememberSaveable {
        mutableStateOf(false)
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // 当前播放器 UI 是否应该显示全屏样式。
    // 注意，这里只看当前屏幕方向，不看 targetFullScreen。
    val currentFullScreen = isLandscape

    // 目标状态和当前状态不一致，说明正在切换横竖屏。
    val isFullScreenChanging = targetFullScreen != currentFullScreen

    // 负责横屏、隐藏状态栏、隐藏导航栏
    PlayerFullScreenEffect(
        targetFullScreen = targetFullScreen,
        currentFullScreen = currentFullScreen
    )
    // 全屏时按返回键，先退出全屏
    BackHandler(
        enabled = targetFullScreen || currentFullScreen
    ) {
        targetFullScreen = false
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            MyVideoPlayer(
                url = currentEpisode?.url.orEmpty(),
                title = movie.name,
//                posterUrl = movie.pic,
                episodes = episodes,
                currentEpisode = currentEpisode,
                onEpisodeSelected = { episode ->
                    currentEpisode = episode
                },
                qualities = listOf("自动", "高清", "超清"),
                currentQuality = "自动",
                onQualitySelected = {
                    // 后面有真实清晰度地址时再处理
                },

                // 这里用当前方向决定 UI。
                isFullScreen = currentFullScreen,

                // 切换过程中隐藏控制栏。
                isFullScreenChanging = isFullScreenChanging,
                // 是否正在退出页面，暂停播放器
                isLeavingPage = isLeavingPage,

                onFullScreenClick = {

                    targetFullScreen = !targetFullScreen
                },
                onBackClick = {
                    if (targetFullScreen || currentFullScreen) {
                        targetFullScreen = false
                    } else {
                        isLeavingPage = true
                        onBackClick()
                    }
                },
                onHomeClick = {
                    targetFullScreen = false
                    onHomeClick()
                },
                onMoreClick = {
                    // 右上角更多按钮
                },
                modifier = if (currentFullScreen) {
                    Modifier.fillMaxSize()
                } else {
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                }
            )

            if (!currentFullScreen) {

                DetailContent(
                    movie = movie,
                    episodes = episodes,
                    currentEpisode = currentEpisode,
                    onEpisodeClick = { episode ->
                        currentEpisode = episode
                    },
                    modifier = Modifier.weight(1f)
                )
            }


        }

    }

}

@Composable
fun DetailContent(
    movie: Movie,
    episodes: List<PlayEpisode>,
    currentEpisode: PlayEpisode?,
    onEpisodeClick: (PlayEpisode) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {
            2
        }
    )

    val coroutineScope = rememberCoroutineScope()

    var showDanmakuInput by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        MovieDetailTabRow(
            pagerState = pagerState,
            commentCount = 2393,
            onTabClick = { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            },
            onDanmakuClick = {
                showDanmakuInput = true
            }
        )

        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            when (page) {
                0 -> {
                    DetailPage(
                        movie = movie,
                        episodes = episodes,
                        currentEpisode = currentEpisode,
                        onEpisodeClick = onEpisodeClick
                    )
                }

                1 -> {
                    CommentPage()
                }
            }
        }

        if (showDanmakuInput) {
            DanmakuInputDialog(
                onDismiss = {
                    showDanmakuInput = false
                },
                onSendClick = { text ->
                    showDanmakuInput = false
                    // TODO 这里调用 viewModel 发送弹幕
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailPage(
    movie: Movie,
    episodes: List<PlayEpisode>,
    currentEpisode: PlayEpisode?,
    onEpisodeClick: (PlayEpisode) -> Unit,
) {
    var showEpisodeSheet by rememberSaveable {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState(
        // 打开后直接完全展开，不停留在半展开状态
        skipPartiallyExpanded = true
    )

    val coroutineScope = rememberCoroutineScope()

    val dismissEpisodeSheet: () -> Unit = {
        coroutineScope
            .launch {
                sheetState.hide()
            }
            .invokeOnCompletion {
                if (!sheetState.isVisible) {
                    showEpisodeSheet = false
                }
            }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                )
        ) {
            item {
                MovieInfoHeader(movie = movie)
            }

            item {
                SpaceOuterHeight()
            }

            item {
                DetailFunctionSection()
            }

            item {
                SpaceOuterHeight()
            }

            item {
                EpisodeSection(
                    episodes = episodes,
                    currentEpisode = currentEpisode,
                    onMoreClick = {
                        showEpisodeSheet = true
                    },
                    onEpisodeClick = onEpisodeClick
                )
            }

            item {
                SpaceOuterHeight()
            }

            item {
                MovieDescriptionSection(
                    description = movie.description.orEmpty()
                )
            }
        }

        if (showEpisodeSheet) {
            EpisodeBottomSheet(
                episodes = episodes,
                currentEpisode = currentEpisode,
                sheetState = sheetState,
                onDismissRequest = {
                    showEpisodeSheet = false
                },
                onCloseClick = dismissEpisodeSheet,
                onEpisodeClick = { episode ->
                    onEpisodeClick(episode)

                    // 选择完成后关闭底部面板
                    dismissEpisodeSheet()
                }
            )
        }
    }
}

@Composable
fun CommentPage() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = "这里显示评论列表",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun DanmakuInputDialog(
    onDismiss: () -> Unit,
    onSendClick: (String) -> Unit
) {
    var text by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("发送弹幕")
        },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                },
                placeholder = {
                    Text("输入弹幕内容")
                },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSendClick(text)
                    }
                }
            ) {
                Text("发送")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("取消")
            }
        }
    )
}

@Preview
@Composable
fun MovieDetailScreenPreview() {
    BLMovieTheme {
        MovieDetailScreen(MovieDetailUiState())
    }
}