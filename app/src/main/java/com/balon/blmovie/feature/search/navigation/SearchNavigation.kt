package com.balon.blmovie.feature.search.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.balon.blmovie.core.model.MovieAdult
import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.feature.search.SearchRoute
import kotlinx.serialization.Serializable


/**
 * 搜索页面路由
 */
object SearchRoutes {

    @Serializable
    data object Search: NavKey
}

/**
 * 跳转到搜索页
 */
fun NavBackStack<NavKey>.navigateToSearch(){
    add(SearchRoutes.Search)
}

/**
 * 注册搜索页
 */
fun EntryProviderScope<NavKey>.searchScreen(
    onBack:()->Unit,
    onMovieItemClick:(Movie)->Unit,
    onMovieAdultItemClick:(MovieAdult)->Unit,
){
    entry<SearchRoutes.Search>{
        SearchRoute(
            onBack = onBack,
            onMovieItemClick = onMovieItemClick,
            onMovieAdultItemClick = onMovieAdultItemClick
        )
    }
}
