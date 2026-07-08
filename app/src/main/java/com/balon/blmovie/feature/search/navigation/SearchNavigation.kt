package com.balon.blmovie.feature.search.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.balon.blmovie.feature.search.SearchRoute


/**
 * 搜索页面路由
 */
object SearchRoutes {

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
    onSearch:()-> Unit
){
    entry<SearchRoutes.Search>{
        SearchRoute(
            onBack = onBack,
            onSearch  = onSearch
        )
    }
}
