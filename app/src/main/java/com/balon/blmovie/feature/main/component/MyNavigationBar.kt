package com.balon.blmovie.feature.main.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.balon.blmovie.core.design.theme.BLMovieTheme
import com.balon.blmovie.core.design.theme.SpaceExtraMedium
import com.balon.blmovie.core.design.theme.SpaceSmallHeight
import com.quick.app.core.extension.clickableNoRipple

/**
 * 底部导航栏
 */
@Composable
fun MyNavigationBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (Int) -> Unit,
    currentDestination: Int,
    modifier: Modifier = Modifier,
): Unit {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .navigationBarsPadding()
    ) {
        destinations.forEachIndexed { index, destination ->
            val selected = (destination.tag == currentDestination)
            val color = if (selected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickableNoRipple {
                        onNavigateToDestination(index)
                    }
                    .padding(vertical = SpaceExtraMedium)
            ) {
                Image(
                    painter = painterResource(
                        id =
                        if (selected)
                            destination.selectedIcon
                        else
                            destination.unselectedIcon
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )

                SpaceSmallHeight()

                Text(
                    text = stringResource(id = destination.titleTextId),
                    style = MaterialTheme.typography.bodySmall,
                    color = color,
                )
            }
        }
    }
}

/**
 * 侧边导航栏
 */
@Composable
fun MyNavigationBarRail(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (Int) -> Unit = {},
    currentDestination: Int? = null,
    modifier: Modifier = Modifier,
): Unit {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(55.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .safeDrawingPadding()
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize(),
        ) {
            destinations.forEachIndexed { index, destination ->
                val selected = destination.tag == currentDestination
                val color = if (selected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickableNoRipple {
                            onNavigateToDestination(index)
                        }
                        .padding(vertical = SpaceExtraMedium),
                ) {
                    Image(
                        painter = painterResource(
                            id =
                            if (selected)
                                destination.selectedIcon
                            else
                                destination.unselectedIcon
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(25.dp)
                    )

                    SpaceSmallHeight()

                    Text(
                        text = stringResource(id = destination.titleTextId),
                        style = MaterialTheme.typography.bodySmall,
                        color = color,
                    )

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyNavigationBarPreview(): Unit {
    BLMovieTheme {
        MyNavigationBar(
            destinations = TopLevelDestination.entries,
            onNavigateToDestination = {},
            currentDestination = TopLevelDestination.HOME.tag,
        )
    }
}

//@Preview(showBackground = false)
//@Composable
//fun MyNavigationBarRailPreview() {
//    BLMovieTheme {
//        MyNavigationBarRail(
//            destinations = TopLevelDestination.entries,
//            currentDestination = TopLevelDestination.entries.first().tag,
//        )
//    }
//}
