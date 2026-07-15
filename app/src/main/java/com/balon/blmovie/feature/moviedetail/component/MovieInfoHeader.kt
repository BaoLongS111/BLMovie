package com.balon.blmovie.feature.moviedetail.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.balon.blmovie.core.design.theme.ArrowIcon
import com.balon.blmovie.core.design.theme.ExpandMoreIcon
import com.balon.blmovie.core.design.theme.SpaceSmall
import com.balon.blmovie.core.model.response.Movie

@Composable
fun MovieInfoHeader(
    movie: Movie,
    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(true)
    }


    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "descriptionArrowRotation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 260,
                    easing = FastOutSlowInEasing
                )
            )
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text(
                text = movie.name ?: "未命名",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = {
                    expanded = !expanded
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "收起" else "展开",
                    modifier = Modifier.rotate(arrowRotation)
                )
            }
        }
        if (expanded) {
            Text(
                text = "${movie.year ?: "1970"} / ${movie.lang ?: "地球语"} / ${movie.typeName} / ${movie.note ?: "这是简介"}",
                color = MaterialTheme.colorScheme.outline,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 6.dp)
            )

            if (movie.area!!.isNotBlank()) {
                InfoRow(text = "地区:${movie.area ?: "地球"}")
            }
            if (movie.director!!.isNotBlank()) {
                InfoRow(text = "导演:${movie.director ?: "导演"}")
            }
            if (movie.actor!!.isNotBlank()) {
                InfoRow(text = "演员:${movie.actor ?: "演员"}")
            }

        }

    }
}

@Composable
fun InfoRow(
    modifier: Modifier = Modifier,
    text: String
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}