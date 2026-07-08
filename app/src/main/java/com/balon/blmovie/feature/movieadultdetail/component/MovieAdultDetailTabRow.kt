package com.balon.blmovie.feature.movieadultdetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.balon.blmovie.R
import com.quick.app.core.extension.clickableNoRipple
import kotlinx.coroutines.launch

@Composable
fun MovieAdultDetailTabRow(
    pagerState: PagerState,
    commentCount: Int,
    onTabClick: (Int) -> Unit,
    onDanmakuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    val indicatorWidth = 24.dp
    val firstTabWidth = 48.dp
    val secondTabWidth = 96.dp

    val pageProgress = (
            pagerState.currentPage + pagerState.currentPageOffsetFraction
            ).coerceIn(0f, 1f)

    val firstIndicatorStart = firstTabWidth / 2 - indicatorWidth / 2
    val secondIndicatorStart = firstTabWidth + secondTabWidth / 2 - indicatorWidth / 2

    val indicatorOffset = firstIndicatorStart +
            (secondIndicatorStart - firstIndicatorStart) * pageProgress

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(firstTabWidth + secondTabWidth)
                .fillMaxHeight()
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.summary),
                    color = if (pagerState.currentPage == 0) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                    },
                    fontWeight = if (pagerState.currentPage == 0) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },
                    modifier = Modifier
                        .width(firstTabWidth)
                        .fillMaxHeight()
                        .clickableNoRipple {
                            coroutineScope.launch {
                                onTabClick(0)
                            }
                        }
                        .wrapContentSize()
                )

                Text(
                    text = "${stringResource(R.string.comment)} $commentCount",
                    color = if (pagerState.currentPage == 1) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                    },
                    fontWeight = if (pagerState.currentPage == 1) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },
                    modifier = Modifier
                        .width(secondTabWidth)
                        .fillMaxHeight()
                        .clickableNoRipple {
                            coroutineScope.launch {
                                onTabClick(1)
                            }
                        }
                        .wrapContentSize()
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = indicatorOffset)
                    .width(indicatorWidth)
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = stringResource(R.string.barrage_message),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier
                .fillMaxHeight()
                .clickableNoRipple {
                    onDanmakuClick()
                }
                .padding(horizontal = 16.dp)
                .wrapContentSize()
        )
    }
}
