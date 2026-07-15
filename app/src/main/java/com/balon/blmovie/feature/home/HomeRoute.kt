package com.balon.blmovie.feature.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import coil3.compose.AsyncImage
import com.balon.blmovie.R
import com.balon.blmovie.core.design.component.DelayedLoadingScreen
import com.balon.blmovie.core.design.component.MyErrorScreen
import com.balon.blmovie.core.design.component.MyLoadingScreen
import com.balon.blmovie.core.design.component.MyPullToRefreshBox
import com.balon.blmovie.core.design.theme.BLMovieTheme
import com.balon.blmovie.core.design.theme.SpaceExtraMedium
import com.balon.blmovie.core.design.theme.SpaceExtraMediumWidth
import com.balon.blmovie.core.design.theme.SpaceExtraOuter2
import com.balon.blmovie.core.design.theme.SpaceExtraSmall2
import com.balon.blmovie.core.design.theme.SpaceLarge
import com.balon.blmovie.core.design.theme.SpaceMedium
import com.balon.blmovie.core.design.theme.SpaceMediumHeight
import com.balon.blmovie.core.design.theme.SpaceMediumWidth
import com.balon.blmovie.core.design.theme.SpaceOuter
import com.balon.blmovie.core.design.theme.SpaceOuterHeight
import com.balon.blmovie.core.design.theme.SpaceSmall
import com.balon.blmovie.core.design.theme.SpaceSmallHeight
import com.balon.blmovie.core.design.theme.SpaceSmallWidth
import com.balon.blmovie.core.design.theme.smallTopRoundedCornerShape
import com.balon.blmovie.core.model.LoadMoreState
import com.balon.blmovie.core.model.PageState
import com.balon.blmovie.core.model.RefreshState
import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.feature.home.component.ActorSection
import com.balon.blmovie.feature.home.component.ArticleSection
import com.balon.blmovie.feature.home.component.Banners
import com.balon.blmovie.feature.home.component.FunctionData
import com.balon.blmovie.feature.home.component.FunctionSection
import com.balon.blmovie.feature.home.component.FunctionUnit
import com.balon.blmovie.feature.home.component.HomeMovieSection
import com.balon.blmovie.util.Constant
import com.hjq.toast.Toaster
import com.quick.app.core.extension.clickableNoRipple
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun HomeRoute(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    viewModel: HomeViewModel = hiltViewModel(),
    onMovieClick: (Movie) -> Unit,
    onSearchClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val errMessage = stringResource(R.string.refresh_error)
    val successMessage: String = stringResource(R.string.refresh_success)
    LaunchedEffect(
        key1 = uiState.refreshState,
    ) {
        if (uiState.refreshState == RefreshState.ERROR) {

            Toaster.show(errMessage)
            viewModel.clearError()
        }

        if (uiState.refreshState == RefreshState.SUCCESS) {

            Toaster.show(successMessage)
            viewModel.clearSuccess()
        }
    }
    HomeScreen(
        uiState = uiState,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        onRefresh = viewModel::refresh,
        onLoadMore = viewModel::loadMore,
        onRetry = viewModel::retry,
        onMovieClick = onMovieClick,
        onSearchClick = onSearchClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    onRefresh: () -> Unit = {},
    onLoadMore: () -> Unit = {},
    onRetry: () -> Unit = {},
    onMovieClick: (Movie) -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val pageState = uiState.pageState
    val banners = uiState.banners
    val japanKoreanMovies = uiState.homeData?.japanKoreanMovie ?: emptyList()  // 日韩动漫
    val actionMovies = uiState.homeData?.actionMovie ?: emptyList()     // 动作片
    val chinaFun = uiState.homeData?.chinaFun ?: emptyList()     // 大陆综艺
    val chinaMovie = uiState.homeData?.chinaMovie ?: emptyList()     // 国产剧
    val actorList = uiState.homeData?.actor ?: emptyList()   // 演员列表
    val articleList = uiState.articleList ?: emptyList()  // 文章列表

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        topBar = {
            MyHomeAppBar(
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onSearchClick = onSearchClick
            )
        },
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
    ) { paddingValues ->

        AnimatedContent(
            targetState = pageState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentKey = { state ->
                state::class
            },
            transitionSpec = {
                /*
                 * Success 页面进入时：
                 * 从下方轻微上移，同时淡入。
                 */
                val enterTransition = when (targetState) {
                    is PageState.Success -> {
                        fadeIn(
                            animationSpec = tween(
                                durationMillis = 320,
                                delayMillis = 40,
                                easing = FastOutSlowInEasing
                            )
                        ) + slideInVertically(
                            animationSpec = tween(
                                durationMillis = 380,
                                easing = FastOutSlowInEasing
                            ),
                            initialOffsetY = { fullHeight ->
                                fullHeight / 12
                            }
                        )
                    }

                    else -> {
                        fadeIn(
                            animationSpec = tween(
                                durationMillis = 220
                            )
                        )
                    }
                }

                /*
                 * Loading 页面快速淡出。
                 * Success 离开时轻微向上移动。
                 */
                val exitTransition = when (initialState) {
                    is PageState.Loading -> {
                        fadeOut(
                            animationSpec = tween(
                                durationMillis = 160
                            )
                        )
                    }

                    is PageState.Success -> {
                        fadeOut(
                            animationSpec = tween(
                                durationMillis = 150
                            )
                        ) + slideOutVertically(
                            animationSpec = tween(
                                durationMillis = 180,
                                easing = FastOutSlowInEasing
                            ),
                            targetOffsetY = { fullHeight ->
                                -fullHeight / 30
                            }
                        )
                    }

                    else -> {
                        fadeOut(
                            animationSpec = tween(
                                durationMillis = 160
                            )
                        )
                    }
                }

                enterTransition
                    .togetherWith(exitTransition)
                    .using(
                        SizeTransform(
                            clip = false
                        )
                    )
            },
            label = "homePageStateAnimation"
        ) { currentPageState ->

            when (currentPageState) {
                is PageState.Loading -> {
                    DelayedLoadingScreen()
                }

                is PageState.Error -> {
                    MyErrorScreen(
                        throwable = currentPageState.throwable,
                        onRetryClick = onRetry
                    )
                }

                is PageState.Success -> {
                    MyPullToRefreshBox(
                        isRefreshing =
                            uiState.refreshState == RefreshState.REFRESHING,
                        onRefresh = onRefresh,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = SpaceExtraMedium)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant
                                )
                        ) {
                            item {
                                Banners(
                                    banners = banners
                                )
                            }

                            item {
                                SpaceMediumHeight()
                            }

                            item {
                                FunctionSection(
                                    list = FunctionData.entries
                                )
                            }

                            item {
                                SpaceOuterHeight()
                            }

                            item {
                                HomeMovieSection(
                                    title = stringResource(
                                        R.string.japanKoreanMovie
                                    ),
                                    movies = japanKoreanMovies,
                                    columnCount = 3,
                                    onMovieClick = onMovieClick
                                )
                            }

                            item {
                                SpaceOuterHeight()
                            }

                            item {
                                HomeMovieSection(
                                    title = stringResource(
                                        R.string.actionMovie
                                    ),
                                    movies = actionMovies,
                                    columnCount = 3,
                                    onMovieClick = onMovieClick
                                )
                            }

                            item {
                                SpaceOuterHeight()
                            }

                            item {
                                HomeMovieSection(
                                    title = stringResource(
                                        R.string.chinaFun
                                    ),
                                    movies = chinaFun,
                                    columnCount = 3,
                                    onMovieClick = onMovieClick
                                )
                            }

                            item {
                                SpaceOuterHeight()
                            }

                            item {
                                HomeMovieSection(
                                    title = stringResource(
                                        R.string.chinaMovie
                                    ),
                                    movies = chinaMovie,
                                    columnCount = 3,
                                    onMovieClick = onMovieClick
                                )
                            }

                            item {
                                SpaceOuterHeight()
                            }

                            item {
                                ActorSection(
                                    actors = actorList
                                )
                            }

                            item {
                                SpaceOuterHeight()
                            }

                            item {
                                ArticleSection(
                                    articles = articleList
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 加载更多
 */
@Composable
fun LoadMoreFooter(
    loadMoreState: LoadMoreState
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (loadMoreState) {
            is LoadMoreState.Loading -> {
                CircularProgressIndicator()
            }

            is LoadMoreState.Error -> {
                Text("加载失败，稍后重试")
            }

            is LoadMoreState.NoMore -> {
                Text("没有更多了")
            }

            else -> Unit
        }
    }
}


/**
 * 标题栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyHomeAppBar(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    onSearchClick: () -> Unit = {},
) {

    val logoModifier = Modifier
        .size(36.dp)
        .padding(horizontal = SpaceSmall)
        .clickableNoRipple {
            // 点击事件
        }

    val finalModifier = if (sharedTransitionScope != null && animatedContentScope != null) {
        with(sharedTransitionScope) {
            logoModifier.sharedElement(
                sharedContentState = rememberSharedContentState(Constant.LOGO_KEY),
                animatedVisibilityScope = animatedContentScope,
                boundsTransform = Constant.LogoBoundsTransform
            )
        }
    } else {
        logoModifier
    }


    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        navigationIcon = {
            Image(
                painter = painterResource(R.drawable.ic_splash_logo),
                contentDescription = null,
                modifier = finalModifier
            )
        },
        title = {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.background)
                    .clickable {
                        onSearchClick()
                    }
                    .padding(horizontal = 14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(22.dp),
                )
                SpaceSmallWidth()
                Text(
                    text = stringResource(id = R.string.search_tip_more),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        },
        actions = {
            Icon(
                painter = painterResource(id = R.drawable.message),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(horizontal = SpaceExtraMedium)
                    .size(28.dp),
            )
        }
    )
}

@Preview
@Composable
fun HomeScreenPreview() {
    BLMovieTheme {
        HomeScreen(
            uiState = HomeUiState(pageState = PageState.Success)
        )
    }
}