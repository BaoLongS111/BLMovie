package com.balon.blmovie.ui

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.SharedTransitionLayout
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
import com.balon.blmovie.feature.moviedetail.navigation.movieDetailScreen
import com.balon.blmovie.feature.moviedetail.navigation.navigateToMovieDetail
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
                    onMovieClick = {it->
                        backStack.navigateToMovieDetail(it.vodId)
                    }
                )
                movieDetailScreen(
                    onBack = { backStack.removeLastOrNull() },
                    onHomeClick = backStack::navigateToMain
                )
            },
            // 正向进入，比如从首页进入详情页
            transitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(260),
                    initialOffsetX = { fullWidth -> fullWidth }
                ) + fadeIn(
                    animationSpec = tween(260)
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(260),
                    targetOffsetX = { fullWidth -> -fullWidth / 4 }
                ) + fadeOut(
                    animationSpec = tween(180)
                )
            },

            // 返回退出，比如从详情页返回首页
            popTransitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(260),
                    initialOffsetX = { fullWidth -> -fullWidth / 4 }
                ) + fadeIn(
                    animationSpec = tween(260)
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(260),
                    targetOffsetX = { fullWidth -> fullWidth }
                ) + fadeOut(
                    animationSpec = tween(180)
                )
            },

            // 手势返回的时候也用同一套
            predictivePopTransitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(260),
                    initialOffsetX = { fullWidth -> -fullWidth / 4 }
                ) + fadeIn(
                    animationSpec = tween(260)
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(260),
                    targetOffsetX = { fullWidth -> fullWidth }
                ) + fadeOut(
                    animationSpec = tween(180)
                )
            }
        )
    }
}