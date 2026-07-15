package com.balon.blmovie.feature.moviedetail.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.balon.blmovie.core.design.theme.BLMovieTheme
import com.balon.blmovie.core.design.theme.SpaceSmall
import com.balon.blmovie.core.design.theme.SpaceSmallHeight

@Composable
fun DetailFunctionSection(
    modifier: Modifier = Modifier
) {

    val sectionShape = RoundedCornerShape(12.dp)

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = sectionShape,

        color = MaterialTheme.colorScheme.surfaceVariant,

        // 轻微的主题层级效果，深色模式也能正常显示
        tonalElevation = 1.dp,

        // 实际阴影，不建议太大
        shadowElevation = 1.dp,

        // 淡淡的描边
        border = BorderStroke(
            width = 0.8.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(
                alpha = 0.65f
            )
        )
    ) {
        // 功能按钮
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 6.dp,
                    vertical = 14.dp
                ),

            ) {
            DetailFunctionData.entries.forEach {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(it.icon),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    SpaceSmallHeight()
                    Text(
                        text = stringResource(it.text),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailFunctionSectionPreview() {
    BLMovieTheme {
        DetailFunctionSection()
    }
}