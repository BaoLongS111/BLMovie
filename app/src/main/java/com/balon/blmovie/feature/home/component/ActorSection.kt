package com.balon.blmovie.feature.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.balon.blmovie.R
import com.balon.blmovie.core.design.theme.SpaceExtraOuter
import com.balon.blmovie.core.design.theme.SpaceExtraOuter2
import com.balon.blmovie.core.design.theme.SpaceMedium
import com.balon.blmovie.core.design.theme.SpaceMediumHeight
import com.balon.blmovie.core.design.theme.SpaceSmall
import com.balon.blmovie.core.design.theme.SpaceSmallHeight
import com.balon.blmovie.core.extension.toImageUrl
import com.balon.blmovie.core.model.Actor
import com.quick.app.core.extension.clickableNoRipple

@Composable
fun ActorSection(
    actors: List<Actor>,
    modifier: Modifier = Modifier,
    onMoreClick: () -> Unit = {},
    onStarClick: (Actor) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        SectionHeader(title = "热映星榜", modifier = modifier, onMoreClick = onMoreClick)
        SpaceMediumHeight()
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            items(actors) { actor ->
                ActorItem(
                    actor = actor,
                    onClick = {
                        onStarClick(actor)
                    }
                )
            }
        }
    }
}

@Composable
fun ActorItem(
    actor: Actor,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .width(72.dp)
            .clickableNoRipple { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = actor.actorPic.toImageUrl(),
            contentDescription = actor.actorName,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.placeholder),
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.DarkGray)
        )

        SpaceSmallHeight()
        Text(
            text = actor.actorName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}