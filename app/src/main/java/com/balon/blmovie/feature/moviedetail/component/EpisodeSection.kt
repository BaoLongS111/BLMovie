package com.balon.blmovie.feature.moviedetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.balon.blmovie.core.model.PlayEpisode

@Composable
fun EpisodeSection(
    episodes: List<PlayEpisode>,
    currentEpisode: PlayEpisode?,
    modifier: Modifier = Modifier,
    onEpisodeClick: (PlayEpisode) -> Unit = {}
) {
    if (episodes.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF151515))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "播放列表",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(episodes) { episode ->
                val selected = episode.url == currentEpisode?.url

                Button(
                    onClick = {
                        onEpisodeClick(episode)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected) {
                            Color(0xFFFF4F9A)
                        } else {
                            Color(0xFF2A2A2A)
                        },
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = episode.name,
                        maxLines = 1
                    )
                }
            }
        }
    }
}