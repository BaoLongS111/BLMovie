package com.balon.blmovie.feature.movieadultdetail.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.balon.blmovie.feature.movieadultdetail.MovieAdultDetailRoute
import kotlinx.serialization.Serializable

/**
 * 影视详情页路由
 */
object MovieAdultDetailRoutes {

    @Serializable
    data class MovieAdultDetail(
        val movieAdultId: Long
    ) : NavKey
}

/**
 * 跳转到影视页
 */
fun NavBackStack<NavKey>.navigateToMovieAdultDetail(movieAdultId: Long) {
    add(
        MovieAdultDetailRoutes.MovieAdultDetail(movieAdultId = movieAdultId)
    )
}

/**
 * 注册影视页
 */
fun EntryProviderScope<NavKey>.movieAdultDetailScreen(
    onBack: () -> Unit,
    onHomeClick: () -> Unit
) {
    entry<MovieAdultDetailRoutes.MovieAdultDetail> { key ->
        MovieAdultDetailRoute(
            navKey = key,
            onBack = onBack,
            onHomeClick = onHomeClick
        )
    }
}

