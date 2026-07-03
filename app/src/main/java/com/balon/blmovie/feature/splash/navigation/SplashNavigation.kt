package com.balon.blmovie.feature.splash.navigation

import androidx.compose.animation.SharedTransitionScope
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.balon.blmovie.feature.splash.SplashRoute
import kotlinx.serialization.Serializable

/**
 * Splash模块路由
 */
object SplashRoutes {
    /**
     * Splash 页面 Key
     */
    @Serializable
    data object Splash : NavKey
}

/**
 * 跳转到Splash页面
 */
fun NavBackStack<NavKey>.navigateToSplash(){
    clear()
    add(SplashRoutes.Splash)
}

/**
 * Splash页面注册
 */
fun EntryProviderScope<NavKey>.splashScreen(
    sharedTransitionScope: SharedTransitionScope,
    toGuide: () -> Unit,
    toMain: () -> Unit
) {
    entry<SplashRoutes.Splash> {
        SplashRoute(
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = LocalNavAnimatedContentScope.current,
            toGuide = toGuide,
            toMain = toMain
        )
    }
}

