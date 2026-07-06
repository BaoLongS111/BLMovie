package com.balon.blmovie.feature.main.component

import com.balon.blmovie.R

/**
 * 顶层界面模型
 */
enum class TopLevelDestination(
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val titleTextId: Int,
    val tag : Int,
) {
    //发现界面
    HOME(
        selectedIcon = R.drawable.tab_home_selected,
        unselectedIcon = R.drawable.tab_home,
        titleTextId = R.string.tab_home,
        tag = R.string.tab_home
    ),
    SHORT_VIDEO(
        selectedIcon = R.drawable.tab_short_video_selected,
        unselectedIcon = R.drawable.tab_short_video,
        titleTextId = R.string.tab_short_video,
        tag = R.string.tab_short_video
    ),
    CATEGORY(
        selectedIcon = R.drawable.tab_category_selected,
        unselectedIcon = R.drawable.tab_category,
        titleTextId = R.string.tab_category,
        tag = R.string.tab_category,
    ),

    ARTICLE(
        selectedIcon = R.drawable.tab_book_selected,
        unselectedIcon = R.drawable.tab_book,
        titleTextId = R.string.tab_article,
        tag = R.string.tab_article,
    ),

    MINE(
        selectedIcon = R.drawable.tab_mine_selected,
        unselectedIcon = R.drawable.tab_mine,
        titleTextId = R.string.tab_mine,
        tag = R.string.tab_mine,
    ),
}