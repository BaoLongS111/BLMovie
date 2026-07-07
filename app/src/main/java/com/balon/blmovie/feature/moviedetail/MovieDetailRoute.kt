package com.balon.blmovie.feature.moviedetail

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.balon.blmovie.core.design.component.MyVideoPlayer
import com.balon.blmovie.core.design.theme.BLMovieTheme
import com.balon.blmovie.core.extension.parsePlayEpisodes
import com.balon.blmovie.core.model.PlayEpisode
import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.feature.moviedetail.component.CurrentPlayUrlSection
import com.balon.blmovie.feature.moviedetail.component.EpisodeSection
import com.balon.blmovie.feature.moviedetail.component.MovieDescriptionSection
import com.balon.blmovie.feature.moviedetail.component.MovieDetailTabRow
import com.balon.blmovie.feature.moviedetail.component.MovieInfoHeader
import com.balon.blmovie.feature.moviedetail.component.PlayerFullScreenEffect

@Composable
fun MovieDetailRoute(
    movieId: Long,
    viewModel: MovieDetailViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    MovieDetailScreen(
        uiState = uiState
    )

    LaunchedEffect(key1 = movieId) {
        viewModel.loadMovieDetail(movieId)
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
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            MyVideoPlayer(
                url = currentEpisode?.url.orEmpty(),
                title = movie.name,
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

                onFullScreenClick = {
                    targetFullScreen = !targetFullScreen
                },
                onBackClick = {
                    if (targetFullScreen || currentFullScreen) {
                        targetFullScreen = false
                    } else {
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        MovieDetailTabRow()
                    }

                    item {
                        MovieInfoHeader(
                            title = movie.name.orEmpty(),
                            note = movie.note.orEmpty()
                        )
                    }

                    item {
                        EpisodeSection(
                            episodes = episodes,
                            currentEpisode = currentEpisode,
                            onEpisodeClick = { episode ->
                                currentEpisode = episode
                            }
                        )
                    }

                    item {
                        CurrentPlayUrlSection(
                            currentEpisode = currentEpisode
                        )
                    }

                    item {
                        MovieDescriptionSection(
                            description = movie.description.orEmpty()
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MovieDetailScreenPreview() {
    BLMovieTheme() {
        MovieDetailScreen(MovieDetailUiState())
    }
}
