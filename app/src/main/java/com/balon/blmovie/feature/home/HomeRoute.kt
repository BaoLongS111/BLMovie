package com.balon.blmovie.feature.home

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
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
import com.balon.blmovie.core.design.component.MyErrorScreen
import com.balon.blmovie.core.design.component.MyLoadingScreen
import com.balon.blmovie.core.design.component.MyPullToRefreshBox
import com.balon.blmovie.core.design.theme.BLMovieTheme
import com.balon.blmovie.core.design.theme.SpaceExtraMedium
import com.balon.blmovie.core.design.theme.SpaceExtraMediumWidth
import com.balon.blmovie.core.design.theme.SpaceLarge
import com.balon.blmovie.core.design.theme.SpaceMedium
import com.balon.blmovie.core.design.theme.SpaceMediumHeight
import com.balon.blmovie.core.design.theme.SpaceMediumWidth
import com.balon.blmovie.core.design.theme.SpaceOuter
import com.balon.blmovie.core.design.theme.SpaceSmall
import com.balon.blmovie.core.design.theme.SpaceSmallWidth
import com.balon.blmovie.core.model.LoadMoreState
import com.balon.blmovie.core.model.PageState
import com.balon.blmovie.core.model.RefreshState
import com.balon.blmovie.core.model.response.Movie
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
    viewModel: HomeViewModel = hiltViewModel()
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
            viewModel.clearError()
        }
    }
    HomeScreen(
        uiState = uiState,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        onRefresh = viewModel::refresh,
        onLoadMore = viewModel::loadMore,
        onRetry = viewModel::retry
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
) {
    val pageState = uiState.pageState
    val banners = uiState.banners
    val japanKoreanMovies = uiState.japanKoreanMovie

    Scaffold(
        topBar = {
            MyHomeAppBar(
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope
            )
        },
        //排除底部导航栏边距
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
//            .exclude(WindowInsets.ime)
    ) { paddingValues ->
        when (pageState) {
            is PageState.Loading -> {
                MyLoadingScreen()
            }

            is PageState.Error -> {
                MyErrorScreen(
                    throwable = pageState.throwable,
                    onRetryClick = onRetry
                )
            }

            is PageState.Success -> {
                MyPullToRefreshBox(
                    isRefreshing = uiState.refreshState == RefreshState.REFRESHING,
                    onRefresh = onRefresh,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    LazyColumn() {
                        // 轮播图
                        item { Banners(banners = banners) }
                        item { HomeMovieSection(japanKoreanMovies = japanKoreanMovies) }
//                        item { LoadMoreFooter(loadMoreState = uiState.loadMoreState) }

                    }

                }
            }
        }

    }
}

/**
 *  {
 *         "dt": "lzm3u8",
 *         "lastTime": "2026-07-04T13:14:55",
 *         "name": "天命动漫",
 *         "note": "更新至第09集",
 *         "pic": "https://img.lzipic.com/upload/vod/20260615-1/6c2192cbe89816cd652381dead9d219b.jpg",
 *         "tid": 52,
 *         "typeName": "AI漫剧",
 *         "vodId": 141341
 *       }
 */
@Composable
fun HomeMovieSection(
    japanKoreanMovies:List<Movie>
){
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(SpaceSmall)
    ){
        SectionHeader()
        SpaceMediumHeight()
        MovieGrid(movies = japanKoreanMovies)
    }

}

@Composable
fun MovieGrid(
    movies: List<Movie>,
    modifier: Modifier = Modifier,
    columnCount: Int = 3,
    onMovieClick: (Movie) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpaceMedium)
    ) {
        movies.chunked(columnCount).forEach { rowMovies ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SpaceMedium)
            ) {
                rowMovies.forEach { movie ->
                    MovieCard(
                        movie = movie,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onMovieClick(movie)
                        }
                    )
                }

                val emptyCount = columnCount - rowMovies.size

                repeat(emptyCount) {
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun MovieCard(
    movie: Movie,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    ElevatedCard(
        modifier = modifier
            .clip(RoundedCornerShape(SpaceMedium))
            .clickable { onClick() },
        shape = RoundedCornerShape(SpaceMedium),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f)
            ) {
                AsyncImage(
                    model = movie.pic,
                    contentDescription = movie.name,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder),
                    modifier = Modifier.fillMaxSize()
                )
                // 图片底部加一个 黑色渐变遮罩层
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                )

                Text(
                    text = movie.note.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 8.dp, vertical = 7.dp)
                )
            }

            Text(
                text = movie.name.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpaceExtraMedium, vertical = SpaceExtraMedium)
            )
        }
    }
}

@Composable
fun SectionHeader(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(
            modifier = Modifier
                .width(SpaceSmall)
                .height(22.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.primary)
        )
        SpaceExtraMediumWidth()
        Text(
            text = "日韩动漫",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        Row(
            modifier = Modifier.clickable {  },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "更多",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

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
            LoadMoreState.LOADING -> {
                CircularProgressIndicator()
            }

            LoadMoreState.ERROR -> {
                Text("加载失败，稍后重试")
            }

            LoadMoreState.NO_MORE -> {
                Text("没有更多了")
            }

            else -> Unit
        }
    }
}

@Composable
fun Banners(
    banners: List<Movie>,
    modifier: Modifier = Modifier
) {

    if (banners.isEmpty()) {
        return
    }

    val pageState = rememberPagerState { banners.size }
    var isUserHolding by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpaceSmall)
            .aspectRatio(16f / 9f)
            .clip(MaterialTheme.shapes.medium)
            .pointerInput(Unit) {
                awaitEachGesture {
                    //监听用户按下和抬起手势
                    awaitFirstDown(requireUnconsumed = false)
                    isUserHolding = true

                    waitForUpOrCancellation()
                    isUserHolding = false
                }
            }
    ) {
        HorizontalPager(
            state = pageState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val item = banners[page]
            Box() {
                AsyncImage(
                    model = item.pic,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(R.drawable.placeholder)
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = SpaceMedium, bottom = SpaceMedium),
                ) {
                    Text(
                        text = item.name.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White
                        )
                    )
                    Text(
                        text = item.note.toString(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White
                        )
                    )
                }
            }

        }
        DotsIndicator(
            dotCount = banners.size,
            type = ShiftIndicatorType(
                dotsGraphic = DotGraphic(
                    size = SpaceSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            ),
            pagerState = pageState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = SpaceSmall)
        )

    }

    // 自动轮播
    val lifeCycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(key1 = lifeCycle, key2 = banners.size) {
        if (banners.size <= 1) {
            return@LaunchedEffect
        }
        lifeCycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) {
                delay(3000L.milliseconds)

                if (isUserHolding || pageState.isScrollInProgress) {
                    continue
                }

                val nextPage = (pageState.currentPage + 1) % banners.size

                try {
                    pageState.animateScrollToPage(nextPage)
                } catch (e: CancellationException) {
                    if (!currentCoroutineContext().isActive) {
                        throw e
                    }
                }
            }
        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyHomeAppBar(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null
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
        navigationIcon = {
            Image(
                painter = painterResource(R.drawable.ic_splash_logo),
                contentDescription = null,
                modifier = finalModifier
            )
        },
        title = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {

                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(22.dp),
                )
                SpaceSmallWidth()
                Text(
                    text = stringResource(id = R.string.search_tip),
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