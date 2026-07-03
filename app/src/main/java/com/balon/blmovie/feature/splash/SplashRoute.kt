package com.balon.blmovie.feature.splash

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.balon.blmovie.R
import com.balon.blmovie.core.design.theme.AppLogoIcon
import com.balon.blmovie.core.design.theme.SpaceMediumHeight
import com.balon.blmovie.util.Constant
import kotlinx.coroutines.delay

@Composable
fun SplashRoute(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    toGuide: () -> Unit,
    toMain: () -> Unit
) {
    SplashScreen(
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        toMain = toMain
    )
}

@Composable
fun SplashScreen(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null,
    toMain: () -> Unit = {}
) {
    // 动画状态
    var startAnimation by rememberSaveable {
        mutableStateOf(false)
    }

    // 启动动画和导航逻辑
    LaunchedEffect(Unit) {
        // 立即启动动画
        startAnimation = true

        // 等动画播放一会儿，再跳转
        delay(3000)

        toMain()
    }

    // Logo Y 轴偏移
    val logoOffsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 160.dp,
        animationSpec = tween(durationMillis = 1000),
        label = "logoOffsetY"
    )

    // Logo 旋转
    val logoRotation by animateFloatAsState(
        targetValue = if (startAnimation) 0f else -15f,
        animationSpec = tween(durationMillis = 1000),
        label = "logoRotation"
    )

    // Logo 透明度
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "logoAlpha"
    )

    // 文字透明度
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = 400
        ),
        label = "textAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        // 背景模糊 Logo
        BackgroundLogoSection()

        // 前景内容
        ForegroundContentSection(
            logoOffsetY = logoOffsetY,
            logoRotation = logoRotation,
            logoAlpha = logoAlpha,
            textAlpha = textAlpha,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope
        )
    }
}

/**
 * 背景 Logo 区域
 */
@Composable
fun BackgroundLogoSection() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .blur(radius = 100.dp)
            .alpha(0.8f)
    ) {
        // 背景大 Logo
        AppLogoIcon(size = 240.dp)
    }
}

/**
 * 前景内容区域
 *
 * @param logoOffsetY Logo Y 轴偏移
 * @param logoRotation Logo 旋转角度
 * @param logoAlpha Logo 透明度
 * @param textAlpha 文字透明度
 */
@Composable
fun ForegroundContentSection(
    logoOffsetY: Dp,
    logoRotation: Float,
    logoAlpha: Float,
    textAlpha: Float,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedContentScope: AnimatedContentScope? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        // 上方空白，把 Logo 推到中间
        Spacer(modifier = Modifier.weight(1f))

        val logoModifier = Modifier
            .offset(y = logoOffsetY)
            .rotate(logoRotation)
            .alpha(logoAlpha)

        val finalLogoModifier =
            if (sharedTransitionScope != null && animatedContentScope != null) {
                with(sharedTransitionScope) {
                    logoModifier.sharedElement(
                        sharedContentState = rememberSharedContentState(key = Constant.LOGO_KEY),
                        animatedVisibilityScope = animatedContentScope,
                        boundsTransform = Constant.LogoBoundsTransform
                    )
                }
            } else {
                logoModifier
            }


        // 前景 Logo
        // 用sharedTransitionScope共享出去
        with(sharedTransitionScope) {
            AppLogoIcon(
                modifier = finalLogoModifier,
                size = 120.dp
            )
        }


        Spacer(modifier = Modifier.weight(1f))

        // 底部文字
        Column(
            modifier = Modifier.alpha(textAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            SpaceMediumHeight()
            Text(
                text = stringResource(R.string.splash_text),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // 底部边距
        Spacer(modifier = Modifier.weight(0.3f))
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen(

    )
}
