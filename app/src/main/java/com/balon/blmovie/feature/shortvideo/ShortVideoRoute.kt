package com.balon.blmovie.feature.shortvideo

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.balon.blmovie.core.design.theme.BLMovieTheme

@Composable
fun ShortVideoRoute() {
    ShortVideoScreen()
}

@Composable
fun ShortVideoScreen(){
    Text(text = "ShortVideoPageShortVideoPageShortVideoPageShortVideoPageShortVideoPageShortVideoPageShortVideoPageShortVideoPage")
}

@Preview
@Composable
fun ShortVideoScreenPreview() {
    BLMovieTheme {
        ShortVideoScreen()
    }
}