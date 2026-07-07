package com.balon.blmovie.feature.moviedetail.component

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
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

@Composable
fun PlayerFullScreenEffect(
    targetFullScreen: Boolean,
    currentFullScreen: Boolean
) {
    val context = LocalContext.current

    val activity = remember(context) {
        context.findActivity()
    }

    /**
     * targetFullScreen 是用户想要的状态。
     *
     * 这里主要负责请求横竖屏。
     * 进入全屏时，先提前隐藏系统栏，避免第一次横屏出现黑色状态栏。
     */
    LaunchedEffect(targetFullScreen, activity) {
        val currentActivity = activity ?: return@LaunchedEffect

        if (targetFullScreen) {
            // 先隐藏状态栏，再请求横屏
            currentActivity.prepareEnterPlayerFullScreen()

            currentActivity.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            // 退出全屏时，先请求竖屏
            // 系统栏等真正回到竖屏后再恢复
            currentActivity.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    /**
     * currentFullScreen 是当前屏幕是否真的已经横屏。
     *
     * 横屏后应用全屏系统栏。
     * 竖屏后恢复普通系统栏。
     */
    LaunchedEffect(currentFullScreen, activity) {
        val currentActivity = activity ?: return@LaunchedEffect

        if (currentFullScreen) {
            currentActivity.applyPlayerLandscapeBars()
        } else {
            currentActivity.applyPlayerPortraitBars()

            // 方向切换结束后再恢复一次，避免状态栏只剩黑条
            delay(120L)
            currentActivity.applyPlayerPortraitBars()
        }
    }

    DisposableEffect(activity) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            // 离开视频详情页时，只恢复系统栏显示和布局方式。
            // 不修改状态栏颜色，也不修改状态栏图标颜色，避免影响首页。
            activity?.applyAppDefaultBars()
        }
    }
}

/**
 * 回到 App 普通页面时的系统栏状态。
 *
 * 这里只做三件事：
 * 1. 内容不要绘制到系统栏下面。
 * 2. 显示状态栏和导航栏。
 * 3. 清理全屏相关 flag。
 *
 * 注意：这里不改状态栏颜色，也不改状态栏图标颜色。
 */
private fun Activity.applyAppDefaultBars() {
    WindowCompat.setDecorFitsSystemWindows(window, true)

    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.show(WindowInsetsCompat.Type.systemBars())

    window.decorView.post {
        WindowCompat.setDecorFitsSystemWindows(window, true)

        val newController = WindowInsetsControllerCompat(window, window.decorView)
        newController.show(WindowInsetsCompat.Type.systemBars())
    }
}

/**
 * 点击全屏后立刻执行。
 *
 * 目的是避免第一次横屏时，系统状态栏短暂显示出来。
 *
 * 注意：这里不修改状态栏颜色。
 */
private fun Activity.prepareEnterPlayerFullScreen() {
    WindowCompat.setDecorFitsSystemWindows(window, false)

    val controller = WindowInsetsControllerCompat(window, window.decorView)

    controller.hide(WindowInsetsCompat.Type.systemBars())

    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    // 有些机型第一次切横屏时，hide 会被系统布局过程覆盖
    // 所以下一帧再隐藏一次
    window.decorView.post {
        val newController = WindowInsetsControllerCompat(window, window.decorView)
        newController.hide(WindowInsetsCompat.Type.systemBars())
        newController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

/**
 * 横屏全屏状态。
 *
 * 注意：这里不修改状态栏颜色。
 */
private fun Activity.applyPlayerLandscapeBars() {
    WindowCompat.setDecorFitsSystemWindows(window, false)

    val controller = WindowInsetsControllerCompat(window, window.decorView)

    controller.hide(WindowInsetsCompat.Type.systemBars())

    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}

/**
 * 竖屏普通状态。
 *
 * 状态栏显示，视频内容从状态栏下面开始。
 *
 * 注意：这里不修改状态栏颜色，也不修改状态栏图标颜色。
 */
private fun Activity.applyPlayerPortraitBars() {
    WindowCompat.setDecorFitsSystemWindows(window, true)

    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

    val controller = WindowInsetsControllerCompat(window, window.decorView)

    controller.show(WindowInsetsCompat.Type.systemBars())

    // 下一帧再恢复一次，防止横竖屏切换覆盖 show
    window.decorView.post {
        WindowCompat.setDecorFitsSystemWindows(window, true)

        val newController = WindowInsetsControllerCompat(window, window.decorView)
        newController.show(WindowInsetsCompat.Type.systemBars())
    }
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
