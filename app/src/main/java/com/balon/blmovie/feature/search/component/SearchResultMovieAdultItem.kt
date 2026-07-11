package com.balon.blmovie.feature.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.balon.blmovie.core.design.theme.SpaceSmall
import com.balon.blmovie.core.extension.toImageUrl
import com.balon.blmovie.core.model.MovieAdult
import com.balon.blmovie.core.model.response.Movie

@Composable
fun SearchResultMovieAdultItem(
    item: MovieAdult,
    modifier: Modifier = Modifier,
    onMovieAdultItemClick: (MovieAdult) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onMovieAdultItemClick(item)
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
                .aspectRatio(16f/9f)
        ) {
            AsyncImage(
                model = item.vodPic.toImageUrl(),
                contentDescription = item.vodName,
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
            val remarks = item.vodTime

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
            text = item.vodName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(
                top = 7.dp,
                start = 2.dp,
                end = 2.dp
            )
        )

    }
}
