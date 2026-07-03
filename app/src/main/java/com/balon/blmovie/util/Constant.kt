package com.balon.blmovie.util

import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.core.tween

/**
 * 常量类
 */
object Constant {

    // shared key
    const val LOGO_KEY = "logo_key"

    // 共享元素动画时长
    const val LOGO_SHARED_ANIMATION_DURATION = 800
    val LogoBoundsTransform = BoundsTransform { initialBounds, targetBounds ->
        tween(
            durationMillis = LOGO_SHARED_ANIMATION_DURATION
        )
    }

}