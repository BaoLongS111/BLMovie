package com.balon.blmovie.core.design.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// 小圆角
val extraSmallRoundedCornerShape = RoundedCornerShape(5.dp)

val myShapes = Shapes(
    extraSmall = extraSmallRoundedCornerShape,
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(30.dp),
)

//左上，右上圆角
val extraSmallTopRoundedCornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
val smallTopRoundedCornerShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)

// 左上，左下圆角（开始圆角）
val largeStartRoundedCornerShape = RoundedCornerShape(
    topStart = 20.dp,
    bottomStart = 20.dp,
    topEnd = 0.dp,
    bottomEnd = 0.dp
)

// 结束圆角
val largeEndRoundedCornerShape = RoundedCornerShape(
    topStart = 0.dp,
    bottomStart = 0.dp,
    topEnd = 20.dp,
    bottomEnd = 20.dp
)



