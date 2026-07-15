package com.balon.blmovie.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.balon.blmovie.R
import com.balon.blmovie.core.design.theme.SpaceExtraMedium
import com.balon.blmovie.core.design.theme.SpaceMedium
import com.balon.blmovie.core.design.theme.SpaceMediumHeight
import com.balon.blmovie.core.design.theme.SpaceSmall
import com.balon.blmovie.core.model.response.Movie

/**
 *  {
 *         "dt": "lzm3u8",
 *         "lastTime": "2026-07-04T13:14:55",
 *         "name": "天命动漫",
 *         "note": "更新至第09集",
 *         "pic": "https://img.lzipic.com/upload/vod/20260615-1/6c2192cbe89816cd652381dead9d219b.jpg",
 *         "tid": 52,
 *         "typeName": "AI漫剧",
 *         "vodId": 141341
 *       }
 */
@Composable
fun HomeMovieSection(
    title: String,
    movies: List<Movie>,
    modifier: Modifier = Modifier,
    columnCount: Int = 3,
    onMovieClick: (Movie) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        SectionHeader(title = title)
        SpaceMediumHeight()
        MovieGrid(
            movies = movies,
            modifier = modifier,
            columnCount = columnCount,
            onMovieClick = onMovieClick
        )
    }

}

/**
 * 网格
 */
@Composable
fun MovieGrid(
    movies: List<Movie>,
    modifier: Modifier = Modifier,
    columnCount: Int = 3,
    onMovieClick: (Movie) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SpaceMedium)
    ) {
        movies.chunked(columnCount).forEach { rowMovies ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SpaceMedium)
            ) {
                rowMovies.forEach { movie ->
                    MovieCard(
                        movie = movie,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onMovieClick(movie)
                        }
                    )
                }

                val emptyCount = columnCount - rowMovies.size

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
 * 影视卡片
 */
@Composable
fun MovieCard(
    movie: Movie,
    modifier: Modifier = Modifier,
    onClick: (Movie) -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(SpaceMedium))
            .clickable { onClick(movie) },
        shape = RoundedCornerShape(SpaceMedium),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f)
            ) {
                AsyncImage(
                    model = movie.pic,
                    contentDescription = movie.name,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.placeholder),
                    modifier = Modifier.fillMaxSize()
                )
                // 图片底部加一个 黑色渐变遮罩层
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.75f)
                                )
                            )
                        )
                )

                Text(
                    text = movie.note.orEmpty(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(horizontal = 8.dp, vertical = 7.dp)
                )
            }

            Text(
                text = movie.name.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpaceExtraMedium, vertical = SpaceExtraMedium)
            )
        }
    }
}