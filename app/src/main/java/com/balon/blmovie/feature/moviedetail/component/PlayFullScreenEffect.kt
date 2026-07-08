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
     * targetFullScreen 表示用户想要进入还是退出全屏。
     * 这里主要负责请求横竖屏。
     */
    LaunchedEffect(targetFullScreen, activity) {
        val currentActivity = activity ?: return@LaunchedEffect

        if (targetFullScreen) {
            // 点击全屏那一刻，先隐藏系统栏，再请求横屏
            currentActivity.prepareEnterPlayerFullScreen()

            currentActivity.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            // 点击退出全屏时，先请求竖屏
            // 系统栏恢复交给下面 currentFullScreen 的监听
            currentActivity.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    /**
     * currentFullScreen 表示屏幕是否真的已经横过来了。
     *
     * 注意这里一定要同时监听 targetFullScreen。
     * 否则第一次点击全屏时，可能 prepare 刚隐藏系统栏，
     * 竖屏恢复逻辑又把系统栏显示出来。
     */
    LaunchedEffect(targetFullScreen, currentFullScreen, activity) {
        val currentActivity = activity ?: return@LaunchedEffect

        when {
            targetFullScreen && currentFullScreen -> {
                // 已经真正横屏，进入完整全屏状态
                currentActivity.applyPlayerLandscapeBars()
            }

            targetFullScreen && !currentFullScreen -> {
                // 正在从竖屏切横屏，继续保持系统栏隐藏
                currentActivity.prepareEnterPlayerFullScreen()

                delay(80L)
                currentActivity.prepareEnterPlayerFullScreen()
            }

            !targetFullScreen && currentFullScreen -> {
                // 正在从横屏切回竖屏
                // 这里不要恢复系统栏，等真的回到竖屏后再恢复
            }

            !targetFullScreen && !currentFullScreen -> {
                // 已经真正回到竖屏，恢复普通状态
                currentActivity.applyPlayerPortraitBars()

                delay(120L)
                currentActivity.applyPlayerPortraitBars()
            }
        }
    }

    DisposableEffect(activity) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            activity?.applyPlayerPortraitBars()
        }
    }
}

/**
 * 点击全屏后立刻执行。
 * 不改状态栏颜色，只隐藏系统栏，并允许内容铺到系统栏区域。
 */
private fun Activity.prepareEnterPlayerFullScreen() {
    WindowCompat.setDecorFitsSystemWindows(window, false)

    val controller = WindowInsetsControllerCompat(window, window.decorView)

    controller.hide(WindowInsetsCompat.Type.systemBars())

    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    window.decorView.post {
        val newController = WindowInsetsControllerCompat(window, window.decorView)
        newController.hide(WindowInsetsCompat.Type.systemBars())
        newController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}

/**
 * 横屏全屏状态。
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
 * 状态栏显示，内容不进入状态栏下面。
 */
private fun Activity.applyPlayerPortraitBars() {
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

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}