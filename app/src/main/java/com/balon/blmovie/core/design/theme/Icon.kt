package com.balon.blmovie.core.design.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.balon.blmovie.R

@Composable
fun HomeIcon() {
    Icon(Icons.Default.Home, contentDescription = null)
}


@Composable
fun ShoppingCartIcon() {
    Icon(Icons.Default.ShoppingCart, contentDescription = null)
}

@Composable
fun ArrowIcon(tint: Color = LocalArrowColor.current) {
    Icon(
        imageVector = Icons.Default.ChevronRight,
        contentDescription = null,
        tint = tint,
    )
}

@Composable
fun ExpandMoreIcon(tint: Color = LocalArrowColor.current) {
    Icon(
        imageVector = Icons.Default.ExpandMore,
        contentDescription = null,
        tint = tint,
    )
}

/**
 * 应用 Logo 图标组件
 *
 * @param modifier 修饰符
 * @param size 图标大小
 */
@Composable
fun AppLogoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp
) {
    Image(
        painter = painterResource(id = R.drawable.ic_splash_logo),
        contentDescription = "Logo",
        modifier = modifier.size(size)
    )
}

