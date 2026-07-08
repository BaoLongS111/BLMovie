package com.balon.blmovie.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import coil3.compose.AsyncImage
import com.balon.blmovie.R
import com.balon.blmovie.core.design.theme.SpaceMedium
import com.balon.blmovie.core.design.theme.SpaceSmall
import com.balon.blmovie.core.model.response.Movie
import com.tbuonomo.viewpagerdotsindicator.compose.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.compose.model.DotGraphic
import com.tbuonomo.viewpagerdotsindicator.compose.type.ShiftIndicatorType
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.milliseconds

/**
 * 轮播图
 */
@Composable
fun Banners(
    banners: List<Movie>,
    modifier: Modifier = Modifier
) {

    val pageState = rememberPagerState { banners.size }
    var isUserHolding by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpaceSmall)
            .aspectRatio(16f / 9f)
            .clip(MaterialTheme.shapes.medium)
            .pointerInput(Unit) {
                awaitEachGesture {
                    //监听用户按下和抬起手势
                    awaitFirstDown(requireUnconsumed = false)
                    isUserHolding = true

                    waitForUpOrCancellation()
                    isUserHolding = false
                }
            }
    ) {
        HorizontalPager(
            state = pageState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val item = banners[page]
            Box() {
                AsyncImage(
                    model = item.pic,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    placeholder = painterResource(R.drawable.placeholder)
                )
                // 黑色渐变遮罩，放在图片上面
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.70f to Color.Transparent,
                                    1f to Color.Black.copy(alpha = 0.45f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = SpaceMedium, bottom = SpaceMedium),
                ) {
                    Text(
                        text = item.name.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White
                        )
                    )
                    Text(
                        text = item.note.toString(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White
                        )
                    )
                }
            }

        }
        DotsIndicator(
            dotCount = banners.size,
            type = ShiftIndicatorType(
                dotsGraphic = DotGraphic(
                    size = SpaceSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            ),
            pagerState = pageState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = SpaceSmall)
        )

    }

    // 自动轮播
    val lifeCycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(key1 = lifeCycle, key2 = banners.size) {
        if (banners.size <= 1) {
            return@LaunchedEffect
        }
        lifeCycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) {
                delay(3000L.milliseconds)

                if (isUserHolding || pageState.isScrollInProgress) {
                    continue
                }

                val nextPage = (pageState.currentPage + 1) % banners.size

                try {
                    pageState.animateScrollToPage(nextPage)
                } catch (e: CancellationException) {
                    if (!currentCoroutineContext().isActive) {
                        throw e
                    }
                }
            }
        }
    }

}