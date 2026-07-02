package com.balon.blmovie.feature.article

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.balon.blmovie.core.design.theme.BLMovieTheme

@Composable
fun ArticleRoute() {
    ArticleScreen()
}

@Composable
fun ArticleScreen(){
    Text(text = "ArticlePageArticlePageArticlePageArticlePageArticlePageArticlePageArticlePageArticlePage")
}

@Preview
@Composable
fun ArticleScreenPreview() {
    BLMovieTheme {
        ArticleScreen()
    }
}