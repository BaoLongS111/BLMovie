package com.balon.blmovie.core.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.balon.blmovie.R
import com.balon.blmovie.core.design.theme.BLMovieTheme
import com.balon.blmovie.core.design.theme.SpaceLargeHeight
import com.balon.blmovie.core.exception.tipData

@Composable
fun MyErrorScreen(
    modifier: Modifier = Modifier,
    throwable: Throwable,
    onRetryClick: () -> Unit = {},
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.network_error)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(180.dp)
            )
            SpaceLargeHeight()
            Text(
                text = throwable.tipData().tipMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            SpaceLargeHeight()
            Button(
                onClick = onRetryClick
            ) {
                Text(
                    text = stringResource(R.string.retry),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }


        }
    }
}

@Preview
@Composable
fun MyErrorScreenPreview() {
    BLMovieTheme() {
        MyErrorScreen(
            throwable = RuntimeException("Text")
        )
    }
}

