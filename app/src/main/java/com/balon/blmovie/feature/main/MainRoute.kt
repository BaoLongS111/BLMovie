package com.balon.blmovie.feature.main

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.balon.blmovie.core.design.theme.BLMovieTheme
import com.balon.blmovie.feature.article.ArticleRoute
import com.balon.blmovie.feature.category.CategoryRoute
import com.balon.blmovie.feature.home.HomeRoute
import com.balon.blmovie.feature.mine.MineRoute
import com.balon.blmovie.feature.shortvideo.ShortVideoRoute
import kotlinx.coroutines.launch

@Composable
fun MainRoute(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    MainScreen(
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope
    )
}

@Composable
fun MainScreen(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null
) {
    //当前选中的界面名称
    var currentDestination by rememberSaveable {
        mutableStateOf(TopLevelDestination.HOME.tag)
    }

    val scope = rememberCoroutineScope()
    //主内容
    val pagerState = rememberPagerState {
        TopLevelDestination.entries.size
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> HomeRoute(
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                )

                1 -> ShortVideoRoute()
                2 -> CategoryRoute()
                3 -> ArticleRoute()
                else -> MineRoute()
            }

        }

        MyNavigationBar(
            destinations = TopLevelDestination.entries,
            onNavigateToDestination = {
                currentDestination = TopLevelDestination.values()[it].tag
                scope.launch {
                    //内容滚动到指定界面
                    pagerState.scrollToPage(it)
                }
            },
            currentDestination = currentDestination,
        )
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    BLMovieTheme {
        MainScreen()
    }
}