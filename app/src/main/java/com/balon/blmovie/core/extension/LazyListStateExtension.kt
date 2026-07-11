package com.balon.blmovie.core.extension

import androidx.compose.foundation.lazy.LazyListState

// 扩展函数用于检查是否到达了底部
fun LazyListState.isScrollingToEnd(): Boolean {
    val totalItems = layoutInfo.totalItemsCount
    val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
    val lastVisibleItemIndex = lastVisibleItem?.index ?: 0
    return totalItems > 0 && lastVisibleItemIndex == totalItems - 1
}