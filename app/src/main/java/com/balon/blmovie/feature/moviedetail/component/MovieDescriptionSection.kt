package com.balon.blmovie.feature.moviedetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MovieDescriptionSection(
    description: String,
    modifier: Modifier = Modifier
) {
    if (description.isBlank()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF151515))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "剧情简介",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}