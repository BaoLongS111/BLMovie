package com.balon.blmovie.feature.moviedetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MovieDetailTabRow(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF151515))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "简介",
            color = Color(0xFFFF4F9A),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 24.dp)
        )

        Text(
            text = "评论 2393",
            color = Color.Gray,
            modifier = Modifier.padding(end = 24.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "点我发弹幕",
            color = Color.Gray
        )
    }
}