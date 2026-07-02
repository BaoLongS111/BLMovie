package com.balon.blmovie.feature.mine

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.balon.blmovie.core.design.theme.BLMovieTheme

@Composable
fun MineRoute() {
    MineScreen()
}

@Composable
fun MineScreen(){
    Text(text = "MinePageMinePageMinePageMinePageMinePageMinePageMinePageMinePage")
}

@Preview
@Composable
fun MineScreenPreview() {
    BLMovieTheme {
        MineScreen()
    }
}