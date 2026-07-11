package com.balon.blmovie.feature.movieadultdetail

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.balon.blmovie.core.design.component.DelayedLoadingScreen
import com.balon.blmovie.core.design.component.MyErrorScreen
import com.balon.blmovie.core.design.component.MyVideoPlayer
import com.balon.blmovie.core.design.component.SimpleVideoPlayer
import com.balon.blmovie.core.design.theme.BLMovieTheme
import com.balon.blmovie.core.extension.parsePlayEpisodes
import com.balon.blmovie.core.model.MovieAdultDetail
import com.balon.blmovie.core.model.PageState
import com.balon.blmovie.core.model.PlayEpisode
import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.feature.movieadultdetail.component.AdultEpisodeSection
import com.balon.blmovie.feature.movieadultdetail.component.MovieAdultDetailTabRow
import com.balon.blmovie.feature.movieadultdetail.component.MovieAdultInfoHeader
import com.balon.blmovie.feature.movieadultdetail.navigation.MovieAdultDetailRoutes
import com.balon.blmovie.feature.moviedetail.component.DetailFunctionSection
import com.balon.blmovie.feature.moviedetail.component.EpisodeSection
import com.balon.blmovie.feature.moviedetail.component.MovieDescriptionSection
import com.balon.blmovie.feature.moviedetail.component.MovieDetailTabRow
import com.balon.blmovie.feature.moviedetail.component.MovieInfoHeader
import com.balon.blmovie.feature.moviedetail.component.PlayerFullScreenEffect
import com.balon.blmovie.util.openBrowser
import com.quick.app.core.extension.clickableNoRipple
import kotlinx.coroutines.launch

@Composable
fun MovieAdultDetailRoute(
    navKey: MovieAdultDetailRoutes.MovieAdultDetail,
    onBack: () -> Unit,
    onHomeClick: () -> Unit,
) {

    val viewModel = hiltViewModel<MovieAdultDetailViewModel, MovieAdultDetailViewModel.Factory>(
        creationCallback = { factory ->
            factory.create(navKey)
        }
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pageState = uiState.pageState

    when (pageState) {
        is PageState.Loading -> {
            DelayedLoadingScreen()
        }

        is PageState.Success -> {
            MovieAdultDetailScreen(
                uiState = uiState,
                onBackClick = onBack,
                onHomeClick = onHomeClick
            )
        }

        is PageState.Error -> {
            MyErrorScreen(
                throwable = pageState.throwable,
                onRetryClick = viewModel::onRetry
            )
        }
    }

}

@Composable
fun MovieAdultDetailScreen(
    uiState: MovieAdultDetailUiState,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val movie = uiState.movieAdultDetail ?: return

    val episodes: List<PlayEpisode> = remember(movie.vodPlayUrl) {
        movie.vodPlayUrl.parsePlayEpisodes()
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
                title = movie.vodName,
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
                AdultDetailContent(
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
fun AdultDetailContent(
    movie: MovieAdultDetail,
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
        MovieAdultDetailTabRow(
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
                    AdultDetailPage(
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

@Composable
fun AdultDetailPage(
    movie: MovieAdultDetail,
    episodes: List<PlayEpisode>,
    currentEpisode: PlayEpisode?,
    onEpisodeClick: (PlayEpisode) -> Unit,
) {
    val context = LocalContext.current
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            MovieAdultInfoHeader(movie = movie)
        }
        item {
            AdultEpisodeSection(
                episodes = episodes,
                currentEpisode = currentEpisode,
                onEpisodeClick = onEpisodeClick
            )
        }
        item {
            Text(
                text = episodes[0].url,
                modifier = Modifier.fillMaxWidth().clickableNoRipple{
                    openBrowser(context, url = episodes[0].url)
                },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 5,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
    BLMovieTheme() {
        MovieAdultDetailScreen(MovieAdultDetailUiState())
    }
}
