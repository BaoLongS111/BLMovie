package com.balon.blmovie.core.design.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPullToRefreshBox(
    /**
     * 当前是否正在刷新。
     *
     * 一般由外部 uiState 控制，例如：
     * isRefreshing = uiState.refreshState == RefreshState.REFRESHING
     */
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    /**
     * 刷新中，Loading 动画固定在顶部的位置。
     *
     * 注意：
     * 这里的单位是 px，不是 dp。
     * 因为 graphicsLayer 的 translationY 使用的是像素值。
     */
    topLoadingOffset: Float = 0f,
    /**
     * 下拉过程中，Loading 动画最多往下移动多少距离。
     *
     * 这里不是触发刷新的距离。
     * 它只控制 Lottie 动画最多向下移动多少。
     *
     * 真正触发刷新的距离由 PullToRefreshBox 内部控制。
     */
    maxDragOffset: Float = 72f,
    /**
     * 松手触发刷新后，Loading 动画回到顶部位置的动画时长。
     *
     * 单位：毫秒
     */
    backToTopDuration: Int = 180,
    /**
     * 刷新结束后，Loading 动画缩小并淡出的动画时长。
     *
     * 单位：毫秒
     */
    exitDuration: Int = 250,
    content: @Composable BoxScope.() -> Unit
) {
    /**
     * PullToRefreshBox 内部使用的状态。
     *
     * 主要用来拿到当前下拉进度：
     * pullToRefreshState.distanceFraction
     */
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        modifier = modifier,

        indicator = {
            /**
             * 是否曾经真正进入过刷新状态。
             *
             * 作用：
             * 当 isRefreshing 从 true 变成 false 时，
             * 不要让 indicator 立刻消失，
             * 而是先播放退出动画。
             */
            var hasRefreshingStarted by remember {
                mutableStateOf(false)
            }

            /**
             * 是否正在播放退出动画。
             *
             * 退出动画就是：
             * scale 1f -> 0f
             * alpha 1f -> 0f
             */
            var isExitAnimating by remember {
                mutableStateOf(false)
            }

            /**
             * 退出动画进度。
             *
             * 1f：正常大小，完全显示
             * 0f：缩小到 0，完全透明
             */
            val exitProgress = remember {
                Animatable(1f)
            }

            /**
             * 监听刷新状态变化。
             *
             * isRefreshing = true：
             * 说明已经触发刷新，重置退出动画状态。
             *
             * isRefreshing = false && hasRefreshingStarted = true：
             * 说明刚刚刷新结束，需要播放退出动画。
             */
            LaunchedEffect(isRefreshing) {
                if (isRefreshing) {
                    /**
                     * 已经进入刷新状态。
                     */
                    hasRefreshingStarted = true

                    /**
                     * 只要正在刷新，就不处于退出动画中。
                     */
                    isExitAnimating = false

                    /**
                     * 重置退出动画进度。
                     *
                     * 保证刷新中的 Loading 是正常大小。
                     */
                    exitProgress.snapTo(1f)
                } else if (hasRefreshingStarted) {
                    /**
                     * 走到这里说明：
                     * 之前刷新过，现在刷新结束了。
                     *
                     * 开始播放退出动画。
                     */
                    isExitAnimating = true

                    /**
                     * 从正常大小开始退出。
                     */
                    exitProgress.snapTo(1f)

                    /**
                     * 执行缩小 + 淡出动画。
                     */
                    exitProgress.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = exitDuration)
                    )

                    /**
                     * 退出动画播放完成。
                     */
                    isExitAnimating = false

                    /**
                     * 本轮刷新流程结束。
                     */
                    hasRefreshingStarted = false

                    /**
                     * 重置进度，给下一次刷新使用。
                     *
                     * 此时 indicator 已经隐藏了，所以不会看到它突然变大。
                     */
                    exitProgress.snapTo(1f)
                }
            }

            /**
             * 当前下拉比例。
             *
             * 0f：没有下拉
             * 0.5f：下拉到触发距离的一半
             * 1f：达到系统默认触发刷新距离
             * 大于 1f：已经超过触发距离，还在继续下拉
             */
            val dragFraction = pullToRefreshState.distanceFraction.coerceAtLeast(0f)

            /**
             * 下拉过程中 Loading 动画的位置。
             */
            val dragTranslationY = topLoadingOffset + (dragFraction * maxDragOffset)
                .coerceAtMost(maxDragOffset)

            /**
             * 是否应该让 Loading 动画固定在顶部。
             *
             * 刷新中：
             * 固定在顶部加载。
             *
             * 退出动画中：
             * 固定在顶部缩小淡出。
             *
             * hasRefreshingStarted 为 true：
             * 用来兜住刷新刚结束的瞬间，
             * 避免 indicator 立刻消失导致闪一下。
             */
            val shouldStayAtTop =
                isRefreshing || isExitAnimating || hasRefreshingStarted

            /**
             * 目标位置。
             */
            val targetTranslationY = if (shouldStayAtTop) {
                topLoadingOffset
            } else {
                dragTranslationY
            }

            /**
             * 回到顶部时使用动画。
             */
            val animatedTranslationY by animateFloatAsState(
                targetValue = targetTranslationY,
                animationSpec = tween(durationMillis = backToTopDuration),
                label = "myPullToRefreshTranslationY"
            )

            /**
             * 最终使用的位置。
             */
            val finalTranslationY = if (shouldStayAtTop) {
                animatedTranslationY
            } else {
                dragTranslationY
            }

            /**
             * 最终缩放比例。
             *
             * 下拉中：不缩放，保持 1f
             * 刷新中：不缩放，保持 1f
             * 退出动画中：根据 exitProgress 从 1f 缩小到 0f
             */
            val finalScale = if (isExitAnimating) {
                exitProgress.value
            } else {
                1f
            }

            /**
             * 最终透明度。
             *
             * 下拉中：不透明，保持 1f
             * 刷新中：不透明，保持 1f
             * 退出动画中：根据 exitProgress 从 1f 淡出到 0f
             */
            val finalAlpha = if (isExitAnimating) {
                exitProgress.value
            } else {
                1f
            }

            /**
             * 是否显示 Loading 指示器。
             *
             * 下拉中：显示
             * 刷新中：显示
             * 刷新结束但退出动画还没播完：显示
             */
            val showIndicator =
                dragFraction > 0f ||
                        isRefreshing ||
                        hasRefreshingStarted ||
                        isExitAnimating

            if (showIndicator) {
                MyLoadingBar(
                    message = null,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .graphicsLayer {
                            /**
                             * 控制上下位置。
                             */
                            translationY = finalTranslationY

                            /**
                             * 控制退出时缩小。
                             */
                            scaleX = finalScale
                            scaleY = finalScale

                            /**
                             * 控制退出时淡出。
                             */
                            alpha = finalAlpha
                        }
                )
            }
        },

        /**
         * 页面内容
         */
        content = content
    )
}