package com.balon.blmovie.core.design.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextOverflow

/**
 * 居中的TopAppBar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCenterTopAppBar(
    title: String = "",
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    actions: @Composable (RowScope.() -> Unit) = {},
    toBack: (() -> Unit)? = null,
) {
    CenterAlignedTopAppBar(
        navigationIcon = {
            toBack?.let {
                IconButton(onClick = toBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = null
                    )
                }
            }
        },
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = actions,
        colors = colors,
    )
}