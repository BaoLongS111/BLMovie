package com.balon.blmovie.feature.moviedetail.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.balon.blmovie.core.design.theme.BLMovieTheme

/**
 * 影视详情页骨架屏
 */
@Composable
fun MovieDetailShimmer(
    modifier: Modifier = Modifier
) {
    val shimmerBrush = rememberShimmerBrush()


    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .statusBarsPadding()
    ) {
        // 顶部视频区域占位，和详情页播放器比例保持一致
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)

                .background(Color.Black)
        )

        Column(
            modifier = modifier
                .padding(horizontal = 20.dp)
                .padding(top = 18.dp)
        ) {

            // 顶部 Tab
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                SkeletonBlock(
                    width = 44.dp,
                    height = 22.dp,
                    brush = shimmerBrush
                )

                Spacer(modifier = Modifier.width(22.dp))

                SkeletonBlock(
                    width = 86.dp,
                    height = 22.dp,
                    brush = shimmerBrush
                )

                Spacer(modifier = Modifier.weight(1f))

                SkeletonBlock(
                    width = 92.dp,
                    height = 22.dp,
                    brush = shimmerBrush
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 影片名称
            SkeletonBlock(
                width = 140.dp,
                height = 24.dp,
                brush = shimmerBrush
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 年份、语言、类型
            SkeletonBlock(
                width = 260.dp,
                height = 15.dp,
                brush = shimmerBrush
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 地区
            SkeletonBlock(
                width = 105.dp,
                height = 15.dp,
                brush = shimmerBrush
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 导演
            SkeletonBlock(
                width = 165.dp,
                height = 15.dp,
                brush = shimmerBrush
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 演员信息
            SkeletonBlock(
                modifier = Modifier.fillMaxWidth(),
                height = 15.dp,
                brush = shimmerBrush
            )

            Spacer(modifier = Modifier.height(10.dp))

            SkeletonBlock(
                width = 310.dp,
                height = 15.dp,
                brush = shimmerBrush
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 点赞、收藏等操作
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                repeat(4) {
                    Column(
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        SkeletonBlock(
                            width = 34.dp,
                            height = 34.dp,
                            shape = CircleShape,
                            brush = shimmerBrush
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SkeletonBlock(
                            width = 20.dp,
                            height = 12.dp,
                            brush = shimmerBrush
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 播放列表标题
            SkeletonBlock(
                width = 110.dp,
                height = 24.dp,
                brush = shimmerBrush
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 选集按钮
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SkeletonBlock(
                    width = 140.dp,
                    height = 58.dp,
                    cornerRadius = 30.dp,
                    brush = shimmerBrush
                )

                SkeletonBlock(
                    width = 140.dp,
                    height = 58.dp,
                    cornerRadius = 30.dp,
                    brush = shimmerBrush
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 剧情简介
            SkeletonBlock(
                width = 115.dp,
                height = 24.dp,
                brush = shimmerBrush
            )

            Spacer(modifier = Modifier.height(18.dp))

            SkeletonBlock(
                modifier = Modifier.fillMaxWidth(),
                height = 15.dp,
                brush = shimmerBrush
            )

            Spacer(modifier = Modifier.height(10.dp))

            SkeletonBlock(
                modifier = Modifier.fillMaxWidth(),
                height = 15.dp,
                brush = shimmerBrush
            )

            Spacer(modifier = Modifier.height(10.dp))

            SkeletonBlock(
                width = 230.dp,
                height = 15.dp,
                brush = shimmerBrush
            )
        }


    }


}

/**
 * 单个骨架占位块
 */
@Composable
private fun SkeletonBlock(
    height: Dp,
    brush: Brush,
    modifier: Modifier = Modifier,
    width: Dp? = null,
    cornerRadius: Dp = 6.dp,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(cornerRadius)
) {
    val finalModifier = if (width != null) {
        modifier.width(width)
    } else {
        modifier
    }

    Box(
        modifier = finalModifier
            .height(height)
            .background(
                brush = brush,
                shape = shape
            )
    )
}

/**
 * 从左向右闪动的 Shimmer 渐变
 */
@Composable
private fun rememberShimmerBrush(): Brush {
    val baseColor = MaterialTheme.colorScheme.onSurface.copy(
        alpha = 0.08f
    )

    val highlightColor = MaterialTheme.colorScheme.onSurface.copy(
        alpha = 0.18f
    )

    val transition = rememberInfiniteTransition(
        label = "skeletonShimmerTransition"
    )

    val translateX by transition.animateFloat(
        initialValue = -700f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1300,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "skeletonShimmerOffset"
    )

    return Brush.linearGradient(
        colors = listOf(
            baseColor,
            highlightColor,
            baseColor
        ),
        start = Offset(
            x = translateX - 300f,
            y = 0f
        ),
        end = Offset(
            x = translateX,
            y = 300f
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun MovieDetailShimmerPreview() {
    BLMovieTheme {
        MovieDetailShimmer()
    }
}