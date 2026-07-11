package com.balon.blmovie.feature.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.balon.blmovie.core.extension.toImageUrl
import com.balon.blmovie.core.model.response.Movie

@Composable
fun SearchResultMovieItem(
    item: Movie,
    modifier: Modifier = Modifier,
    onMovieItemClick: (Movie) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onMovieItemClick(item)
            }
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        /*
         * 海报区域。
         *
         * 使用 Box 是为了在海报右上角和右下角叠加文字。
         * 外层 LazyVerticalGrid 会决定每个卡片的实际宽度。
         */
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.72f)
        ) {
            AsyncImage(
                model = item.pic.toImageUrl(),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth()
            )

            /*
             * 右上角分类标签。
             * 比如爱情、动漫、综艺。
             */
            if (item.typeName.isNotBlank()) {
                Text(
                    text = item.typeName,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .background(
                            color = Color.Black.copy(alpha = 0.55f),
                            shape = RoundedCornerShape(
                                bottomStart = 6.dp
                            )
                        )
                        .padding(
                            horizontal = 6.dp,
                            vertical = 3.dp
                        )
                )
            }

            /*
             * 右下角更新状态。
             * 比如更新至第2期、TC中字、已完结。
             */
            val remarks = item.note.orEmpty()

            if (remarks.isNotBlank()) {
                Text(
                    text = remarks,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .background(
                            color = Color.Black.copy(alpha = 0.58f),
                            shape = RoundedCornerShape(
                                topStart = 6.dp
                            )
                        )
                        .padding(
                            horizontal = 6.dp,
                            vertical = 3.dp
                        )
                )
            }
        }

        /*
         * 影片名称。
         */
        Text(
            text = item.name.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(
                top = 7.dp,
                start = 2.dp,
                end = 2.dp
            )
        )

        /*
         * 底部辅助信息。
         * 优先显示地区、年份和演员。
         */
        val description = buildList {
            item.area
                ?.takeIf { it.isNotBlank() }
                ?.let(::add)

            item.year
                ?.takeIf { it.isNotBlank() }
                ?.let(::add)

            item.actor
                ?.takeIf { it.isNotBlank() }
                ?.let(::add)
        }.joinToString(" / ")

        if (description.isNotBlank()) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(
                    top = 3.dp,
                    start = 2.dp,
                    end = 2.dp
                )
            )
        }
    }
}