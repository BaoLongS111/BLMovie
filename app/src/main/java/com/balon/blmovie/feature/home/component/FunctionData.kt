package com.balon.blmovie.feature.home.component

import androidx.compose.ui.res.stringResource
import com.balon.blmovie.R

enum class FunctionData(
    val icon: Int,
    val text: Int
) {
    AiYou(
        icon = R.drawable.aiyoutengmang,
        text = R.string.aiyoutengmang
    ),
    Netflix(
        icon = R.drawable.netflix,
        text = R.string.netflix
    ),
    HotFilm(
        icon = R.drawable.reyingsudi,
        text = R.string.reyingsudi
    ),
    Rank(
        icon = R.drawable.paihangbang,
        text = R.string.paihangbang
    ),
    ComingSoon(
        icon = R.drawable.jijiangshangying,
        text = R.string.jijiangshangying
    ),
    FreeNovel(
        icon = R.drawable.mianfeixiaoshuo,
        text = R.string.mianfeixiaoshuo
    ),
    Comic(
        icon = R.drawable.kanmanhua,
        text = R.string.kanmanhua
    ),
    Music(
        icon = R.drawable.tingyingyue,
        text = R.string.tingyinyue
    ),
    ListeningNovel(
        icon = R.drawable.tingxiaoshuo,
        text = R.string.tingxiaoshuo
    ),
    Welfare(
        icon = R.drawable.fulizhongxin,
        text = R.string.fulizhongxin
    )
}