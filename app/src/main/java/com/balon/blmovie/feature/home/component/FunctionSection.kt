package com.balon.blmovie.feature.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.balon.blmovie.core.design.theme.BLMovieTheme
import com.balon.blmovie.core.design.theme.SpaceExtraMedium
import com.balon.blmovie.core.design.theme.SpaceExtraSmall2
import com.balon.blmovie.core.design.theme.SpaceMedium
import com.balon.blmovie.core.design.theme.SpaceOuter
import com.balon.blmovie.core.design.theme.SpaceSmallHeight

@Composable
fun FunctionSection(
    list: List<FunctionData>,
    modifier: Modifier = Modifier,
    columnCount: Int = 5,
    onFunctionClick: (FunctionData) -> Unit = {}
) {
    val sectionShape = RoundedCornerShape(16.dp)

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = sectionShape,

        color = MaterialTheme.colorScheme.surfaceVariant,

        // 轻微的主题层级效果，深色模式也能正常显示
        tonalElevation = 1.dp,

        // 实际阴影，不建议太大
        shadowElevation = 3.dp,

        // 淡淡的描边
        border = BorderStroke(
            width = 0.8.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(
                alpha = 0.65f
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 12.dp,
                    vertical = 14.dp
                ),
            verticalArrangement = Arrangement.spacedBy(SpaceOuter)
        ) {
            list.chunked(columnCount).forEach { rowFunctions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SpaceMedium)
                ) {
                    rowFunctions.forEach { function ->
                        FunctionUnit(
                            data = function,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                onFunctionClick(function)
                            }
                        )
                    }

                    val emptyCount = columnCount - rowFunctions.size

                    repeat(emptyCount) {
                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 功能按钮最小组件
 */
@Composable
fun FunctionUnit(
    data: FunctionData,
    modifier: Modifier = Modifier,
    onClick: (FunctionData) -> Unit = {}
) {
    Column(
        modifier = modifier.clickable {
            onClick(data)
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    MaterialTheme.colorScheme.primary
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(data.icon),
                contentDescription = null,
                modifier = Modifier.size(21.dp)
            )
        }
        SpaceSmallHeight()
        Text(
            text = stringResource(data.text),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FunctionSectionPreview(){
    BLMovieTheme() {
        FunctionSection(FunctionData.entries)
    }
}