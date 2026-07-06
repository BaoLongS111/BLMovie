package com.balon.blmovie.feature.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.balon.blmovie.R
import com.balon.blmovie.core.design.theme.SpaceMedium
import com.balon.blmovie.core.design.theme.SpaceMediumHeight
import com.balon.blmovie.core.design.theme.SpaceMediumWidth
import com.balon.blmovie.core.design.theme.SpaceSmall
import com.balon.blmovie.core.design.theme.SpaceSmallHeight
import com.balon.blmovie.core.extension.toImageUrl
import com.balon.blmovie.core.model.Article

@Composable
fun ArticleSection(
    articles: List<Article>,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit = {},
    onArticleClick: (Article) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SpaceSmall)
    ) {
        SectionHeader(title = "文章资讯", onMoreClick = onMoreClick)

        SpaceMediumHeight()

        Column(
            modifier = modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SpaceMedium)
        ) {
            articles.forEach { article ->
                ArticleItem(
                    article = article,
                    onClick = {
                        onArticleClick(article)
                    }
                )
            }
        }
    }
}


@Composable
fun ArticleItem(
    article: Article,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(SpaceMedium),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SpaceSmall),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = article.artPic.toImageUrl(),
                contentDescription = article.artName,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.placeholder),
                modifier = Modifier
                    .width(120.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(SpaceSmall))
            )

            SpaceMediumWidth()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(80.dp)
            ) {
                Text(
                    text = article.artName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                SpaceSmallHeight()

                Text(
                    text = article.artBlurb.replace("\n","").trimStart().trimEnd(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = article.artTime.orEmpty(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}