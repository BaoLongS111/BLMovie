package com.balon.blmovie.feature.moviedetail.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.balon.blmovie.feature.moviedetail.MovieDetailRoute
import kotlinx.serialization.Serializable

/**
 * 影视详情页路由
 */
object MovieDetailRoutes {

    @Serializable
    data class MovieDetail(
        val movieId: Long
    ) : NavKey
}

/**
 * 跳转到影视页
 */
fun NavBackStack<NavKey>.navigateToMovieDetail(movieId: Long) {
    add(
        MovieDetailRoutes.MovieDetail(movieId = movieId)
    )
}

/**
 * 注册影视页
 */
fun EntryProviderScope<NavKey>.movieDetailScreen(
    onBack: () -> Unit,
    onHomeClick: () -> Unit
) {
    entry<MovieDetailRoutes.MovieDetail> { key ->
        MovieDetailRoute(
            navKey = key,
            onBack = onBack,
            onHomeClick = onHomeClick
        )
    }
}

