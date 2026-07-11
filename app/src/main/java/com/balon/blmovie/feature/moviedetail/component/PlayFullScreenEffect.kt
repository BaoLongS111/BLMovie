package com.balon.blmovie.feature.moviedetail.component

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.coroutines.delay

/**
 * 保存进入详情页时原本的系统栏样式。
 *
 * 全屏过程中会临时把状态栏背景改成黑色、图标改成浅色。
 * 退出全屏或离开页面时，再恢复原来的样式，避免影响主页。
 */
private data class SystemBarStyle(
    val statusBarColor: Int,
    val navigationBarColor: Int,
    val lightStatusBars: Boolean,
    val lightNavigationBars: Boolean
)

@Composable
fun PlayerFullScreenEffect(
    targetFullScreen: Boolean,
    currentFullScreen: Boolean
) {
    val context = LocalContext.current
    val activity = remember(context) {
        context.findPlayerActivity()
    }

    val originalSystemBarStyle = remember(activity) {
        activity?.captureSystemBarStyle()
    }

    /**
     * 这是状态变化后的兜底处理。
     *
     * 真正点击全屏时，还要在点击回调里调用
     * requestEnterPlayerFullScreenImmediately()。
     * 这样系统栏会在修改 Compose 状态前立即开始隐藏。
     */
    LaunchedEffect(targetFullScreen, activity) {
        val currentActivity = activity ?: return@LaunchedEffect

        if (targetFullScreen) {
            currentActivity.requestEnterPlayerFullScreenImmediately()
        } else {
            // 先请求竖屏，等真实方向变成竖屏后再恢复系统栏。
            currentActivity.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    /**
     * 根据真实屏幕方向维持系统栏状态。
     */
    LaunchedEffect(
        targetFullScreen,
        currentFullScreen,
        activity,
        originalSystemBarStyle
    ) {
        val currentActivity = activity ?: return@LaunchedEffect

        when {
            targetFullScreen -> {
                // 进入全屏途中以及真正横屏后，都持续保持沉浸式状态。
                currentActivity.keepPlayerImmersive()

                // 部分设备第一次旋转时会重新显示一次系统栏，下一帧再隐藏。
                delay(32L)
                currentActivity.keepPlayerImmersive()
            }

            !targetFullScreen && currentFullScreen -> {
                // 正在从横屏返回竖屏，暂时继续保持沉浸式，避免中途闪出系统栏。
                currentActivity.keepPlayerImmersive()
            }

            !targetFullScreen && !currentFullScreen -> {
                // 已经真正回到竖屏，再恢复普通系统栏。
                currentActivity.restorePlayerPortraitBars(originalSystemBarStyle)

                delay(80L)
                currentActivity.restorePlayerPortraitBars(originalSystemBarStyle)
            }
        }
    }

    DisposableEffect(activity, originalSystemBarStyle) {
        onDispose {
            activity?.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            activity?.restorePlayerPortraitBars(originalSystemBarStyle)
        }
    }
}

/**
 * 必须在点击全屏按钮的回调里直接调用。
 *
 * 处理顺序：
 * 1. 立即让内容进入系统栏区域
 * 2. 临时把系统栏背景变成黑色
 * 3. 立即隐藏系统栏
 * 4. 请求横屏
 *
 * 这样视觉上会更接近常见视频应用：
 * 状态栏区域先变黑，随后在旋转过程中消失。
 */
internal fun Activity.requestEnterPlayerFullScreenImmediately() {
    // 先让播放器内容可以布局到系统栏下面。
    WindowCompat.setDecorFitsSystemWindows(window, false)

    // 系统栏尚未完全隐藏的极短时间内显示黑色，避免出现白色闪条。
    window.statusBarColor = Color.BLACK
    window.navigationBarColor = Color.BLACK

    val controller = WindowCompat.getInsetsController(
        window,
        window.decorView
    )

    controller.isAppearanceLightStatusBars = false
    controller.isAppearanceLightNavigationBars = false
    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    // WindowInsetsController 是主要方式。
    controller.hide(WindowInsetsCompat.Type.systemBars())

    // FLAG_FULLSCREEN 作为部分设备第一次进入全屏时的兜底。
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

    // 在隐藏系统栏之后请求横屏。
    requestedOrientation =
        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE

    // 下一帧再隐藏一次，防止方向切换过程重新应用系统栏。
    window.decorView.postOnAnimation {
        keepPlayerImmersive()
    }
}

/**
 * 横屏以及横竖屏切换途中，持续保持沉浸式状态。
 */
private fun Activity.keepPlayerImmersive() {
    WindowCompat.setDecorFitsSystemWindows(window, false)

    val controller = WindowCompat.getInsetsController(
        window,
        window.decorView
    )

    controller.isAppearanceLightStatusBars = false
    controller.isAppearanceLightNavigationBars = false
    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    controller.hide(WindowInsetsCompat.Type.systemBars())
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

/**
 * 真正回到竖屏后恢复系统栏。
 */
private fun Activity.restorePlayerPortraitBars(
    originalStyle: SystemBarStyle?
) {
    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

    WindowCompat.setDecorFitsSystemWindows(window, true)

    val controller = WindowCompat.getInsetsController(
        window,
        window.decorView
    )

    controller.show(WindowInsetsCompat.Type.systemBars())

    // 恢复进入详情页时原本的颜色和图标明暗，避免影响主页。
    originalStyle?.let { style ->
        window.statusBarColor = style.statusBarColor
        window.navigationBarColor = style.navigationBarColor
        controller.isAppearanceLightStatusBars = style.lightStatusBars
        controller.isAppearanceLightNavigationBars =
            style.lightNavigationBars
    }

    window.decorView.postOnAnimation {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        WindowCompat.getInsetsController(
            window,
            window.decorView
        ).show(WindowInsetsCompat.Type.systemBars())
    }
}

private fun Activity.captureSystemBarStyle(): SystemBarStyle {
    val controller = WindowCompat.getInsetsController(
        window,
        window.decorView
    )

    return SystemBarStyle(
        statusBarColor = window.statusBarColor,
        navigationBarColor = window.navigationBarColor,
        lightStatusBars = controller.isAppearanceLightStatusBars,
        lightNavigationBars =
            controller.isAppearanceLightNavigationBars
    )
}

internal tailrec fun Context.findPlayerActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findPlayerActivity()
        else -> null
    }
}