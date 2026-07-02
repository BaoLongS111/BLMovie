package com.balon.blmovie.feature.category

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.balon.blmovie.core.design.theme.BLMovieTheme

@Composable
fun CategoryRoute() {
    CategoryScreen()
}

@Composable
fun CategoryScreen(){
    Text(text = "CategoryPageCategoryPageCategoryPageCategoryPageCategoryPageCategoryPageCategoryPageCategoryPage")
}

@Preview
@Composable
fun CategoryScreenPreview() {
    BLMovieTheme {
        CategoryScreen()
    }
}