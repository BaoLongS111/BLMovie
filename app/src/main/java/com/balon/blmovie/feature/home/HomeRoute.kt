package com.balon.blmovie.feature.home

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.balon.blmovie.R
import com.balon.blmovie.core.design.theme.BLMovieTheme
import com.balon.blmovie.core.design.theme.SpaceExtraMedium
import com.balon.blmovie.core.design.theme.SpaceSmall
import com.balon.blmovie.core.design.theme.SpaceSmallWidth
import com.balon.blmovie.util.Constant
import com.quick.app.core.extension.clickableNoRipple

@Composable
fun HomeRoute(
    sharedTransitionScope: SharedTransitionScope?=null,
    animatedContentScope: AnimatedContentScope?=null
) {
    HomeScreen(
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null
) {
    Scaffold(
        topBar = {
            MyHomeAppBar(
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope
            )
        },
        //排除底部导航栏边距
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
//            .exclude(WindowInsets.ime)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "HomePageHomePageHomePageHomePageHomePageHomePageHomePageHomePage")
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyHomeAppBar(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null
) {

    val logoModifier = Modifier
        .size(36.dp)
        .padding(horizontal = SpaceSmall)
        .clickableNoRipple {
            // 点击事件
        }

    val finalModifier = if (sharedTransitionScope != null && animatedContentScope != null) {
        with(sharedTransitionScope) {
            logoModifier.sharedElement(
                sharedContentState = rememberSharedContentState(Constant.LOGO_KEY),
                animatedVisibilityScope = animatedContentScope,
                boundsTransform = Constant.LogoBoundsTransform
            )
        }
    } else {
        logoModifier
    }


    TopAppBar(
        navigationIcon = {
            Image(
                painter = painterResource(R.drawable.ic_splash_logo),
                contentDescription = null,
                modifier = finalModifier
            )
        },
        title = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {

                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(22.dp),
                )
                SpaceSmallWidth()
                Text(
                    text = stringResource(id = R.string.search_tip),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        },
        actions = {
            Icon(
                painter = painterResource(id = R.drawable.message),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(horizontal = SpaceExtraMedium)
                    .size(28.dp),
            )
        }
    )
}

@Preview
@Composable
fun HomeScreenPreview() {
    BLMovieTheme {
        HomeScreen()
    }
}