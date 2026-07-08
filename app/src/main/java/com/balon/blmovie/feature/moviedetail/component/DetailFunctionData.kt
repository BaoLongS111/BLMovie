package com.balon.blmovie.feature.moviedetail.component

import com.balon.blmovie.R

enum class DetailFunctionData(
    val icon: Int,
    val selectedIcon:Int,
    val text: Int
) {
    Like(
        icon = R.drawable.like,
        selectedIcon = R.drawable.like_selected,
        text = 0
    ),
    Dislike(
        icon = R.drawable.dislike,
        selectedIcon = R.drawable.dislike_selected,
        text = 0
    ),
    Fav(
        icon = R.drawable.fav,
        selectedIcon = R.drawable.fav_selected,
        text = 0
    ),
    Share(
        icon = R.drawable.share,
        selectedIcon = R.drawable.share_selected,
        text = 0
    )
}