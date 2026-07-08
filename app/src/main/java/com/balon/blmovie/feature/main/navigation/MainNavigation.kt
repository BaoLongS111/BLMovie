package com.balon.blmovie.feature.main.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.feature.main.MainRoute
import kotlinx.serialization.Serializable

/**
 * Main模块路由
 */
object MainRoutes {
    /**
     * Main 页面Key
     */
    @Serializable
    data object Main : NavKey
}

/**
 * 跳转到Main
 */
fun NavBackStack<NavKey>.navigateToMain(){
    clear()
    add(MainRoutes.Main)
}

/**
 * Main 页面注册
 */
fun EntryProviderScope<NavKey>.mainScreen(
    sharedTransitionScope: SharedTransitionScope,
    onMovieClick:(Movie)-> Unit,
    onSearchClick:()->Unit,
) {
    entry<MainRoutes.Main> {
        MainRoute(
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = LocalNavAnimatedContentScope.current,
            onMovieClick = onMovieClick,
            onSearchClick = onSearchClick
        )
    }
}
