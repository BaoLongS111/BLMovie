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
     * targetFullScreen 表示用户想进入全屏还是退出全屏。
     * 这里负责请求横竖屏。
     */
    LaunchedEffect(targetFullScreen, activity) {
        val currentActivity = activity ?: return@LaunchedEffect

        if (targetFullScreen) {
            /**
             * 点击全屏那一刻，先隐藏系统栏。
             * 这样可以减少第一次横屏时状态栏短暂显示的问题。
             */
            currentActivity.prepareEnterPlayerFullScreen()

            currentActivity.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            /**
             * 点击退出全屏时，先请求竖屏。
             * 系统栏恢复等真正回到竖屏后再做。
             */
            currentActivity.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    /**
     * currentFullScreen 表示屏幕是否真的已经横过来了。
     *
     * 注意这里同时监听 targetFullScreen 和 currentFullScreen。
     * 不然第一次点击全屏时，刚隐藏系统栏，又可能被竖屏恢复逻辑显示出来。
     */
    LaunchedEffect(targetFullScreen, currentFullScreen, activity) {
        val currentActivity = activity ?: return@LaunchedEffect

        when {
            targetFullScreen && currentFullScreen -> {
                // 已经真正横屏，进入完整全屏状态
                currentActivity.applyPlayerLandscapeBars()
            }

            targetFullScreen && !currentFullScreen -> {
                // 正在从竖屏切到横屏，继续保持系统栏隐藏
                currentActivity.prepareEnterPlayerFullScreen()

                delay(80L)
                currentActivity.prepareEnterPlayerFullScreen()
            }

            !targetFullScreen && currentFullScreen -> {
                /**
                 * 正在从横屏切回竖屏。
                 * 这里不要立刻恢复系统栏。
                 * 等 currentFullScreen 变成 false 后再恢复。
                 */
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
 *
 * 不改状态栏颜色。
 * 只隐藏系统栏，并允许内容铺到系统栏区域。
 */
fun Activity.prepareEnterPlayerFullScreen() {
    /**
     * 这个 flag 比 WindowInsetsController.hide 更早生效。
     * 用它兜底解决第一次全屏状态栏闪一下的问题。
     */
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

    WindowCompat.setDecorFitsSystemWindows(window, false)

    val controller = WindowInsetsControllerCompat(window, window.decorView)

    controller.hide(WindowInsetsCompat.Type.systemBars())

    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    /**
     * 有些机型第一次切横屏时，hide 会被系统布局流程覆盖。
     * 下一帧再隐藏一次。
     */
    window.decorView.post {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val newController = WindowInsetsControllerCompat(window, window.decorView)

        newController.hide(WindowInsetsCompat.Type.systemBars())

        newController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    /**
     * 再延迟隐藏一次，进一步减少真机第一次全屏时状态栏闪一下。
     */
    window.decorView.postDelayed(
        {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

            val newController = WindowInsetsControllerCompat(window, window.decorView)

            newController.hide(WindowInsetsCompat.Type.systemBars())

            newController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        },
        80L
    )
}

/**
 * 横屏全屏状态。
 */
private fun Activity.applyPlayerLandscapeBars() {
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

    WindowCompat.setDecorFitsSystemWindows(window, false)

    val controller = WindowInsetsControllerCompat(window, window.decorView)

    controller.hide(WindowInsetsCompat.Type.systemBars())

    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}

/**
 * 竖屏普通状态。
 *
 * 状态栏显示，内容不进入状态栏下面。
 * 不修改状态栏颜色和状态栏图标颜色。
 */
private fun Activity.applyPlayerPortraitBars() {
    WindowCompat.setDecorFitsSystemWindows(window, true)

    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

    val controller = WindowInsetsControllerCompat(window, window.decorView)

    controller.show(WindowInsetsCompat.Type.systemBars())

    /**
     * 下一帧再恢复一次。
     * 防止横竖屏切换过程中 show 被系统覆盖。
     */
    window.decorView.post {
        WindowCompat.setDecorFitsSystemWindows(window, true)

        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val newController = WindowInsetsControllerCompat(window, window.decorView)

        newController.show(WindowInsetsCompat.Type.systemBars())
    }

    /**
     * 再延迟恢复一次。
     * 主要处理部分真机退出全屏后状态栏显示不稳定的问题。
     */
    window.decorView.postDelayed(
        {
            WindowCompat.setDecorFitsSystemWindows(window, true)

            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

            val newController = WindowInsetsControllerCompat(window, window.decorView)

            newController.show(WindowInsetsCompat.Type.systemBars())
        },
        120L
    )
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}