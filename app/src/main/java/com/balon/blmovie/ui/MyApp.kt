package com.balon.blmovie.ui

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.animation.SharedTransitionLayout
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
                if (backStack.lastOrNull() == MainRoutes.Main) {
                    // 这里可以直接退出 Activity，或者什么都不做
                    backStack.removeLastOrNull()
                } else if (backStack.size > 1) {
                    backStack.removeLastOrNull()
                }
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                splashScreen(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    toGuide = {
                        // 后面你有 Guide 页面时，在这里跳 Guide
                        // backStack.add(GuideRoutes.Guide)
                    },
                    toMain = backStack::navigateToMain
                )

                mainScreen(
                    sharedTransitionScope = this@SharedTransitionLayout,
                )
            }
        )
    }
}