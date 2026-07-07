package com.balon.blmovie.feature.moviedetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quick.app.core.extension.clickableNoRipple

@Composable
fun MovieDetailTabRow(
    selectedIndex: Int,
    commentCount: Int,
    onTabClick: (Int) -> Unit,
    onDanmakuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf("简介", "评论 $commentCount")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, title ->
                Text(
                    text = title,
                    color = if (selectedIndex == index) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f)
                    },
                    fontWeight = if (selectedIndex == index) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Normal
                    },
                    modifier = Modifier
                        .clickable {
                            onTabClick(index)
                        }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }

        Text(
            text = "点我发弹幕",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            modifier = Modifier
                .clickableNoRipple {
                    onDanmakuClick()
                }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}