package com.balon.blmovie.feature.moviedetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.balon.blmovie.core.model.PlayEpisode

@Composable
fun CurrentPlayUrlSection(
    currentEpisode: PlayEpisode?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF151515))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "当前播放地址",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = currentEpisode?.url.orEmpty(),
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )
    }
}