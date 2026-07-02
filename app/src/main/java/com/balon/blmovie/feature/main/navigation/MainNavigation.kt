package com.balon.blmovie.feature.main.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
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
fun EntryProviderScope<NavKey>.mainScreen() {
    entry<MainRoutes.Main> {
        MainRoute()
    }
}
