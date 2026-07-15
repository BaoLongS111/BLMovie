package com.balon.blmovie.feature.moviedetail.component

import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.balon.blmovie.core.design.theme.BLMovieTheme

@Composable
fun MovieDetailSkeletonScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background)

    ) {
        // 顶部视频区域占位，和详情页播放器比例保持一致
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Color.Black)
        )

        // 简介 / 评论 / 发弹幕那一行
        SkeletonTabRow()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 16.dp,
                vertical = 14.dp
            )
        ) {
            item {
                // 标题
                SkeletonBox(
                    width = 130.dp,
                    height = 22.dp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 副标题信息
                SkeletonBox(
                    width = 230.dp,
                    height = 14.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                SkeletonBox(
                    width = 160.dp,
                    height = 14.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                SkeletonBox(
                    width = 260.dp,
                    height = 14.dp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 点赞、踩、收藏、分享那一行
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    repeat(4) {
                        Column {
                            SkeletonBox(
                                width = 26.dp,
                                height = 26.dp,
                                shape = CircleShape
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            SkeletonBox(
                                width = 18.dp,
                                height = 10.dp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 播放列表标题
                SkeletonBox(
                    width = 90.dp,
                    height = 20.dp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 选集按钮
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(3) {
                        SkeletonBox(
                            width = 76.dp,
                            height = 40.dp,
                            shape = RoundedCornerShape(50)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // 剧情简介标题
                SkeletonBox(
                    width = 90.dp,
                    height = 20.dp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 剧情简介内容
                SkeletonBox(
                    width = 320.dp,
                    height = 14.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                SkeletonBox(
                    width = 300.dp,
                    height = 14.dp
                )

                Spacer(modifier = Modifier.height(8.dp))

                SkeletonBox(
                    width = 240.dp,
                    height = 14.dp
                )
            }
        }
    }
}

@Composable
private fun SkeletonTabRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        SkeletonBox(
            width = 42.dp,
            height = 16.dp
        )

        Spacer(modifier = Modifier.width(22.dp))

        SkeletonBox(
            width = 78.dp,
            height = 16.dp
        )

        Spacer(modifier = Modifier.weight(1f))

        SkeletonBox(
            width = 88.dp,
            height = 16.dp
        )
    }
}

@Composable
private fun SkeletonBox(
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(6.dp)
) {
    val brush = rememberSkeletonBrush()

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(shape)
            .background(brush)
    )
}

@Composable
private fun rememberSkeletonBrush(): Brush {
    val baseColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    val highlightColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)

    val transition = rememberInfiniteTransition(
        label = "skeletonTransition"
    )

    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1100,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "skeletonTranslate"
    )

    return Brush.linearGradient(
        colors = listOf(
            baseColor,
            highlightColor,
            baseColor
        ),
        start = Offset(
            x = translate - 300f,
            y = translate - 300f
        ),
        end = Offset(
            x = translate,
            y = translate
        )
    )
}
@Preview(showBackground = true)
@Composable
fun MovieDetailSkeletonScreenPreview(){
    BLMovieTheme{
        MovieDetailSkeletonScreen()
    }
}