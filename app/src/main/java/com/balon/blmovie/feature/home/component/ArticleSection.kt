package com.balon.blmovie.feature.home.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.balon.blmovie.R
import com.balon.blmovie.core.design.theme.BLMovieTheme
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
        modifier = modifier.fillMaxWidth()
    ) {
        SectionHeader(
            title = "文章资讯",
            onMoreClick = onMoreClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
    val articleBlurb = remember(article.artBlurb) {
        article.artBlurb
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(
            width = 0.6.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(
                alpha = 0.65f
            )
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ArticleCover(
                imageUrl = article.artPic.toImageUrl(),
                contentDescription = article.artName
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(88.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = article.artName,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (articleBlurb.isNotBlank()) {
                    Text(
                        text = articleBlurb,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ArticleTypeLabel(
                        text = article.typeName
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        imageVector = Icons.Rounded.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = article.artTime.subSequence(0,10).toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ArticleCover(
    imageUrl: String,
    contentDescription: String?
) {
    Box(
        modifier = Modifier
            .width(124.dp)
            .height(88.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                MaterialTheme.colorScheme.surfaceVariant
            )
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder),
            modifier = Modifier.matchParentSize()
        )
    }
}

@Composable
private fun ArticleTypeLabel(
    text: String
) {
    if (text.isBlank()) return

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                MaterialTheme.colorScheme.primary
            )
            .padding(
                horizontal = 7.dp,
                vertical = 3.dp
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ArticleSectionPreview() {
    BLMovieTheme {
        ArticleSection(
            modifier = Modifier.padding(16.dp),
            articles = listOf(
                Article(
                    artAuthor = "BaLon",
                    artBlurb = "这是模糊搜索，这里可以展示文章的简单介绍内容，让用户快速了解文章主题。",
                    artFrom = "",
                    artId = 78327,
                    artName = "这是一篇文章资讯的标题",
                    artPic = "",
                    artTime = "2026-07-15",
                    typeId = 44,
                    typeName = "娱乐新闻",
                    artRemarks = ""
                ),
                Article(
                    artAuthor = "BaLon",
                    artBlurb = "热门影视资讯以及最新影视动态。",
                    artFrom = "",
                    artId = 78328,
                    artName = "本周热门影视资讯盘点",
                    artPic = "",
                    artTime = "2026-07-14",
                    typeId = 44,
                    typeName = "影视资讯",
                    artRemarks = ""
                )
            )
        )
    }
}