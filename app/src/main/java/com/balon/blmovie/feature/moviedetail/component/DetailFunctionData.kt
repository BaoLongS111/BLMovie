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
        text = R.string.good
    ),
    Dislike(
        icon = R.drawable.dislike,
        selectedIcon = R.drawable.dislike_selected,
        text = R.string.bad
    ),
    Fav(
        icon = R.drawable.fav,
        selectedIcon = R.drawable.fav_selected,
        text = R.string.favourite
    ),
    Share(
        icon = R.drawable.share,
        selectedIcon = R.drawable.share_selected,
        text = R.string.share
    )
}