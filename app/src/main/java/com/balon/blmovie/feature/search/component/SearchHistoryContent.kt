package com.balon.blmovie.feature.search.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import com.balon.blmovie.core.design.theme.BLMovieTheme
import com.quick.app.core.extension.clickableNoRipple
import kotlin.math.max

private const val COLLAPSED_HISTORY_LINES = 2

private val HistoryHorizontalSpacing = 5.dp

/**
 * 历史标签每一行之间的距离。
 * 原来是 10.dp，视觉上偏松，现在收紧为 4.dp。
 */
private val HistoryVerticalSpacing = 0.dp

private val HistoryActionButtonSize = 32.dp
private val HistoryActionIconSize = 20.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchHistoryContent(
    historyList: List<String> = emptyList(),
    hotKeywords: List<String> = emptyList(),
    modifier: Modifier = Modifier,
    onKeywordClick: (String) -> Unit = {},
    onClearHistoryClick: () -> Unit = {}
) {
    var historyExpanded by rememberSaveable {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {
        item(
            key = "history_title"
        ) {
            SectionTitleRow(
                title = "历史记录",
                action = {
                    if (historyList.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(HistoryActionButtonSize)
                                .clickableNoRipple {
                                    historyExpanded = false
                                    onClearHistoryClick()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "清空历史",
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(
                                    HistoryActionIconSize
                                )
                            )
                        }
                    }
                }
            )
        }

        item(
            key = "history_content"
        ) {
            if (historyList.isEmpty()) {
                EmptyHistoryContent()
            } else {
                CollapsibleHistoryChips(
                    historyList = historyList,
                    expanded = historyExpanded,
                    onExpandedChange = {
                        historyExpanded = it
                    },
                    onKeywordClick = onKeywordClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }

        item(
            key = "recommend_title"
        ) {
            SectionTitleRow(
                title = "猜你喜欢",
                modifier = Modifier.padding(
                    top = 12.dp
                )
            )
        }

        items(
            items = hotKeywords.chunked(2),
            key = { row ->
                row.joinToString(separator = "_")
            }
        ) { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                row.forEach { keyword ->
                    Text(
                        text = keyword,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .clickableNoRipple {
                                onKeywordClick(keyword)
                            }
                    )
                }

                if (row.size == 1) {
                    Box(
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "暂无历史记录",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun CollapsibleHistoryChips(
    historyList: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onKeywordClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) {
            180f
        } else {
            0f
        },
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        ),
        label = "historyArrowRotation"
    )

    SubcomposeLayout(
        modifier = modifier.animateContentSize(
            animationSpec = tween(
                durationMillis = 320,
                easing = FastOutSlowInEasing
            )
        )
    ) { constraints ->
        val horizontalSpacingPx =
            HistoryHorizontalSpacing.roundToPx()

        val verticalSpacingPx =
            HistoryVerticalSpacing.roundToPx()

        val childConstraints = constraints.copy(
            minWidth = 0,
            minHeight = 0
        )

        val chipPlaceables = historyList.mapIndexed { index, history ->
            subcompose(
                slotId = "history_chip_$index"
            ) {
                SearchHistoryChip(
                    text = history,
                    onClick = {
                        onKeywordClick(history)
                    }
                )
            }.first().measure(childConstraints)
        }

        val arrowPlaceable = subcompose(
            slotId = "history_expand_button"
        ) {
            HistoryExpandButton(
                expanded = expanded,
                rotation = arrowRotation,
                onClick = {
                    onExpandedChange(!expanded)
                }
            )
        }.first().measure(childConstraints)

        val layoutWidth = constraints.maxWidth

        val naturalRows = buildHistoryRows(
            placeables = chipPlaceables,
            maxWidth = layoutWidth,
            horizontalSpacingPx = horizontalSpacingPx,
            firstRowReservedWidth = 0
        )

        val canExpand =
            naturalRows.size > COLLAPSED_HISTORY_LINES

        val rows = if (canExpand) {
            buildHistoryRows(
                placeables = chipPlaceables,
                maxWidth = layoutWidth,
                horizontalSpacingPx = horizontalSpacingPx,
                firstRowReservedWidth =
                    arrowPlaceable.width + horizontalSpacingPx
            )
        } else {
            naturalRows
        }

        val visibleRows = if (
            canExpand &&
            !expanded
        ) {
            rows.take(COLLAPSED_HISTORY_LINES)
        } else {
            rows
        }

        val contentHeight = visibleRows.foldIndexed(
            initial = 0
        ) { index, height, row ->
            height +
                    row.height +
                    if (index < visibleRows.lastIndex) {
                        verticalSpacingPx
                    } else {
                        0
                    }
        }

        val firstRowHeight =
            visibleRows.firstOrNull()?.height ?: 0

        val finalHeight = constraints.constrainHeight(
            max(
                contentHeight,
                if (canExpand) {
                    arrowPlaceable.height
                } else {
                    0
                }
            )
        )

        layout(
            width = layoutWidth,
            height = finalHeight
        ) {
            var rowY = 0

            visibleRows.forEachIndexed { rowIndex, row ->
                var itemX = 0

                row.indices.forEach { placeableIndex ->
                    val placeable =
                        chipPlaceables[placeableIndex]

                    placeable.placeRelative(
                        x = itemX,
                        y = rowY
                    )

                    itemX +=
                        placeable.width +
                                horizontalSpacingPx
                }

                rowY += row.height

                if (rowIndex < visibleRows.lastIndex) {
                    rowY += verticalSpacingPx
                }
            }

            if (canExpand) {
                arrowPlaceable.placeRelative(
                    x = layoutWidth - arrowPlaceable.width,
                    y = (
                            firstRowHeight -
                                    arrowPlaceable.height
                            ).coerceAtLeast(0) / 2
                )
            }
        }
    }
}

private fun buildHistoryRows(
    placeables: List<Placeable>,
    maxWidth: Int,
    horizontalSpacingPx: Int,
    firstRowReservedWidth: Int
): List<HistoryRowMeasure> {
    if (placeables.isEmpty()) {
        return emptyList()
    }

    val rows = mutableListOf<HistoryRowMeasure>()
    var currentRow = HistoryRowMeasure()

    placeables.forEachIndexed { index, placeable ->
        val currentRowIndex = rows.size

        val availableWidth = if (currentRowIndex == 0) {
            (maxWidth - firstRowReservedWidth)
                .coerceAtLeast(0)
        } else {
            maxWidth
        }

        val nextWidth = if (currentRow.indices.isEmpty()) {
            placeable.width
        } else {
            currentRow.width +
                    horizontalSpacingPx +
                    placeable.width
        }

        if (
            currentRow.indices.isNotEmpty() &&
            nextWidth > availableWidth
        ) {
            rows += currentRow
            currentRow = HistoryRowMeasure()
        }

        currentRow.indices += index
        currentRow.width = if (currentRow.indices.size == 1) {
            placeable.width
        } else {
            currentRow.width +
                    horizontalSpacingPx +
                    placeable.width
        }
        currentRow.height =
            max(currentRow.height, placeable.height)
    }

    if (currentRow.indices.isNotEmpty()) {
        rows += currentRow
    }

    return rows
}

private data class HistoryRowMeasure(
    val indices: MutableList<Int> = mutableListOf(),
    var width: Int = 0,
    var height: Int = 0
)

@Composable
private fun HistoryExpandButton(
    expanded: Boolean,
    rotation: Float,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(HistoryActionButtonSize)
            .clickableNoRipple {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = if (expanded) {
                "收起历史记录"
            } else {
                "展开历史记录"
            },
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .size(HistoryActionIconSize)
                .rotate(rotation)
        )
    }
}

@Composable
private fun SearchHistoryChip(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.widthIn(
            max = 210.dp
        ),
        shape = RoundedCornerShape(45),
        color = MaterialTheme.colorScheme.background,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 8.dp
            )
        )
    }
}

@Composable
private fun SectionTitleRow(
    title: String,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier.weight(1f)
        )

        action?.invoke()
    }
}

@Preview(showBackground = true)
@Composable
fun SearchHistoryContentPreview() {
    BLMovieTheme {
        SearchHistoryContent(
            listOf(
                "你好", "你好", "你好",
                "你好", "你好", "你好", "你好"
            )
        )
    }
}