package com.balon.blmovie.feature.main.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
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
    add(MainRoutes.Main)
}

/**
 * Main 页面注册
 */
fun EntryProviderScope<NavKey>.mainScreen(
    sharedTransitionScope: SharedTransitionScope
) {
    entry<MainRoutes.Main> {
        MainRoute(
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = LocalNavAnimatedContentScope.current
        )
    }
}
