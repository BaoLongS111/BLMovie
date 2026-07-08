package com.balon.blmovie.ui

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.balon.blmovie.feature.main.navigation.MainRoutes
import com.balon.blmovie.feature.main.navigation.mainScreen
import com.balon.blmovie.feature.main.navigation.navigateToMain
import com.balon.blmovie.feature.movieadultdetail.navigation.movieAdultDetailScreen
import com.balon.blmovie.feature.movieadultdetail.navigation.navigateToMovieAdultDetail
import com.balon.blmovie.feature.moviedetail.navigation.movieDetailScreen
import com.balon.blmovie.feature.moviedetail.navigation.navigateToMovieDetail
import com.balon.blmovie.feature.search.navigation.navigateToSearch
import com.balon.blmovie.feature.search.navigation.searchScreen
import com.balon.blmovie.feature.splash.navigation.SplashRoutes
import com.balon.blmovie.feature.splash.navigation.splashScreen

/**
 * 全局功能
 */
@Composable
fun MyApp() {

    val backStack = rememberNavBackStack(SplashRoutes.Splash)
    SharedTransitionLayout {
        NavDisplay(
            backStack = backStack,
            onBack = {
                if (backStack.size > 1) {
                    backStack.removeLastOrNull()
                }
//                if (backStack.lastOrNull() == MainRoutes.Main) {
//                    // 这里可以直接退出 Activity，或者什么都不做
//                    backStack.removeLastOrNull()
//                }
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                splashScreen(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    toGuide = {
                        // 后面你有 Guide页面时，在这里跳 Guide
                        // backStack.add(GuideRoutes.Guide)
                    },
                    toMain = backStack::navigateToMain
                )

                mainScreen(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    onMovieClick = { it ->
                        backStack.navigateToMovieDetail(it.vodId)
                    },
                    onSearchClick = backStack::navigateToSearch
                )
                movieDetailScreen(
                    onBack = { backStack.removeLastOrNull() },
                    onHomeClick = backStack::navigateToMain
                )

                movieAdultDetailScreen(
                    onBack = { backStack.removeLastOrNull() },
                    onHomeClick = backStack::navigateToMain
                )
                searchScreen(
                    onBack = { backStack.removeLastOrNull() },
                    onMovieItemClick = { it->
                        backStack.navigateToMovieDetail(it.vodId)
                    },
                    onMovieAdultItemClick = {it->
                        backStack.navigateToMovieAdultDetail(it.vodId)
                    }
                )

            },
            transitionSpec = {
                (
                        slideInHorizontally(
                            animationSpec = tween(
                                durationMillis = 320,
                                easing = FastOutSlowInEasing
                            ),
                            initialOffsetX = { fullWidth -> fullWidth }
                        ) togetherWith ExitTransition.None
                        ).apply {
                        targetContentZIndex = 1f
                    }
            },
            popTransitionSpec = {
                (
                        EnterTransition.None togetherWith slideOutHorizontally(
                            animationSpec = tween(
                                durationMillis = 320,
                                easing = FastOutSlowInEasing
                            ),
                            targetOffsetX = { fullWidth -> fullWidth }
                        )
                        ).apply {
                        targetContentZIndex = -1f
                    }
            },

            // 手势返回的时候也用同一套
            predictivePopTransitionSpec = {
                // 不做预测返回预览动画
                EnterTransition.None togetherWith ExitTransition.None
            }
        )
    }
}