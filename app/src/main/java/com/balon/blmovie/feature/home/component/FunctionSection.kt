package com.balon.blmovie.feature.home.component

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.balon.blmovie.core.design.theme.SpaceExtraMedium
import com.balon.blmovie.core.design.theme.SpaceExtraSmall2
import com.balon.blmovie.core.design.theme.SpaceMedium
import com.balon.blmovie.core.design.theme.SpaceOuter
import com.balon.blmovie.core.design.theme.SpaceSmallHeight

@Composable
fun FunctionSection(
    list:List<FunctionData>,
    modifier: Modifier = Modifier,
    columnCount:Int = 5,
    onFunctionClick: (FunctionData) -> Unit = {}
){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SpaceExtraSmall2, vertical = SpaceMedium),
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
                .size(36.dp)
                .clip(RoundedCornerShape(SpaceExtraMedium))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(data.icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
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