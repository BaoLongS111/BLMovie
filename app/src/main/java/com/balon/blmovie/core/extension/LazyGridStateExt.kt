package com.balon.blmovie.core.extension

import androidx.compose.foundation.lazy.grid.LazyGridState

/**
 * 判断 LazyVerticalGrid 是否接近列表底部。
 *
 * remainingItems 表示距离底部还剩多少项时开始加载。
 * 默认剩余 4 项时提前加载下一页。
 */
/**
 * 判断网格是否接近电影数据的底部。
 *
 * 这里只使用电影数据数量计算触发位置，
 * 不会把 Footer 当成普通电影数据。
 */
fun LazyGridState.shouldLoadMore(
    dataItemCount: Int,
    prefetchItemCount: Int
): Boolean {
    if (dataItemCount <= 0) {
        return false
    }

    val lastVisibleIndex =
        layoutInfo.visibleItemsInfo
            .lastOrNull()
            ?.index
            ?: return false

    val lastDataItemIndex =
        dataItemCount - 1

    val triggerIndex = (
            lastDataItemIndex -
                    prefetchItemCount +
                    1
            ).coerceAtLeast(0)

    return lastVisibleIndex >= triggerIndex
}