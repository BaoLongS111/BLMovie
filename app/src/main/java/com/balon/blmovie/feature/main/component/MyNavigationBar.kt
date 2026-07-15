package com.balon.blmovie.feature.main.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.balon.blmovie.core.design.theme.BLMovieTheme
import com.balon.blmovie.core.design.theme.SpaceExtraMedium
import com.balon.blmovie.core.design.theme.SpaceSmallHeight
import com.quick.app.core.extension.clickableNoRipple

/**
 * 底部导航栏
 *
 * 整个导航栏中只有一个选中胶囊。
 * 页面切换时，胶囊会平滑移动到当前选项。
 */
@Composable
fun MyNavigationBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (Int) -> Unit,
    currentDestination: Int,
    modifier: Modifier = Modifier,
) {
    if (destinations.isEmpty()) {
        return
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp
        ),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp,
        shadowElevation = 10.dp,
        border = BorderStroke(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(
                alpha = 0.45f
            )
        )
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    horizontal = 8.dp,
                    vertical = 6.dp
                )
        ) {
            /*
             * 找到当前选中项的位置。
             */
            val selectedIndex = remember(
                destinations,
                currentDestination
            ) {
                destinations.indexOfFirst {
                    it.tag == currentDestination
                }.coerceAtLeast(0)
            }

            /*
             * 每个导航项平均占用的宽度。
             */
            val itemWidth = maxWidth / destinations.size

            /*
             * 选中胶囊的宽高。
             */
            val indicatorWidth = 52.dp
            val indicatorHeight = 34.dp

            /*
             * 计算胶囊移动到当前选项时的横向位置。
             */
            val targetIndicatorOffset = (
                    itemWidth.value * selectedIndex +
                            (itemWidth.value - indicatorWidth.value) / 2f
                    ).dp

            /*
             * 胶囊只做平滑滑动，不使用弹簧回弹。
             * 这样可以防止胶囊左右抖动。
             */
            val indicatorOffset by animateDpAsState(
                targetValue = targetIndicatorOffset,
                animationSpec = tween(
                    durationMillis = 280,
                    easing = FastOutSlowInEasing
                ),
                label = "navigationIndicatorOffset"
            )

            /*
             * 全部导航项共用的唯一胶囊。
             *
             * y = 4.dp 是为了与导航按钮内部的顶部间距保持一致。
             */
            Box(
                modifier = Modifier
                    .offset(
                        x = indicatorOffset,
                        y = 4.dp
                    )
                    .width(indicatorWidth)
                    .height(indicatorHeight)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(
                            alpha = 0.13f
                        ),
                        shape = RoundedCornerShape(17.dp)
                    )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                destinations.forEachIndexed { index, destination ->
                    AnimatedNavigationItem(
                        destination = destination,
                        selected = destination.tag == currentDestination,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onNavigateToDestination(index)
                        }
                    )
                }
            }
        }
    }
}

/**
 * 带单次回弹动画的导航按钮。
 */
@Composable
private fun AnimatedNavigationItem(
    destination: TopLevelDestination,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isPressed by interactionSource.collectIsPressedAsState()

    /*
     * 按下时轻微缩小。
     *
     * 这里使用普通的时间动画，不使用弹簧。
     * 避免按下动画和选中动画同时回弹。
     */
    val pressedScale by animateFloatAsState(
        targetValue = if (isPressed) {
            0.92f
        } else {
            1f
        },
        animationSpec = tween(
            durationMillis = 90,
            easing = FastOutSlowInEasing
        ),
        label = "navigationPressedScale"
    )

    /*
     * 选中时的单次回弹动画。
     */
    val selectedScale = remember {
        Animatable(1f)
    }

    /*
     * 防止页面第一次打开时，默认选中的按钮自动弹一下。
     */
    var initialized by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(selected) {
        if (!initialized) {
            initialized = true
            selectedScale.snapTo(1f)
            return@LaunchedEffect
        }

        if (selected) {
            /*
             * 先稍微缩小。
             */
            selectedScale.snapTo(0.94f)

            /*
             * 然后放大到 1.08，最后稳定回到 1。
             *
             * 使用固定关键帧，不会像弹簧一样反复震荡。
             */
            selectedScale.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 260

                    1.08f at 110 using FastOutSlowInEasing
                    1f at 260
                }
            )
        } else {
            selectedScale.snapTo(1f)
        }
    }

    val finalScale = pressedScale * selectedScale.value

    val iconColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onBackground
        },
        animationSpec = tween(durationMillis = 180),
        label = "navigationIconColor"
    )

    val textColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onBackground
        },
        animationSpec = tween(durationMillis = 180),
        label = "navigationTextColor"
    )

    Column(
        modifier = modifier
            .selectable(
                selected = selected,
                interactionSource = interactionSource,
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .padding(
                horizontal = 2.dp,
                vertical = 4.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /*
         * 这里保持固定宽高。
         * 胶囊在外层单独移动，这里不再创建背景。
         */
        Box(
            modifier = Modifier
                .width(52.dp)
                .height(34.dp),
            contentAlignment = Alignment.Center
        ) {
            /*
             * selectedIcon 和 unselectedIcon 使用淡入淡出切换。
             * 可以减轻两个图标形状不同造成的跳动。
             */
            Crossfade(
                targetState = selected,
                animationSpec = tween(durationMillis = 120),
                label = "navigationIconCrossfade"
            ) { isSelected ->
                Icon(
                    painter = painterResource(
                        id = if (isSelected) {
                            destination.selectedIcon
                        } else {
                            destination.unselectedIcon
                        }
                    ),
                    contentDescription = stringResource(
                        id = destination.titleTextId
                    ),
                    tint = iconColor,
                    modifier = Modifier
                        .size(23.dp)
                        .graphicsLayer {
                            scaleX = finalScale
                            scaleY = finalScale
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = stringResource(
                id = destination.titleTextId
            ),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * 侧边导航栏
 */
@Composable
fun MyNavigationBarRail(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (Int) -> Unit = {},
    currentDestination: Int? = null,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(55.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            destinations.forEachIndexed { index, destination ->
                val selected = destination.tag == currentDestination

                val color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }

                Column(
                    modifier = Modifier
                        .clickableNoRipple {
                            onNavigateToDestination(index)
                        }
                        .padding(vertical = SpaceExtraMedium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(
                            id = if (selected) {
                                destination.selectedIcon
                            } else {
                                destination.unselectedIcon
                            }
                        ),
                        contentDescription = stringResource(
                            id = destination.titleTextId
                        ),
                        modifier = Modifier.size(25.dp)
                    )

                    SpaceSmallHeight()

                    Text(
                        text = stringResource(
                            id = destination.titleTextId
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = color
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyNavigationBarPreview() {
    BLMovieTheme {
        MyNavigationBar(
            destinations = TopLevelDestination.entries,
            onNavigateToDestination = {},
            currentDestination = TopLevelDestination.HOME.tag
        )
    }
}