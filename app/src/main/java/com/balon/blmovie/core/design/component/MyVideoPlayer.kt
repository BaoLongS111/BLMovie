package com.balon.blmovie.core.design.component

import android.app.Activity
import android.graphics.Bitmap
import android.content.Intent
import android.content.IntentFilter
import android.content.Context
import android.content.ContextWrapper
import android.media.AudioManager
import android.os.BatteryManager
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import androidx.media3.inspector.frame.FrameExtractor
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import com.balon.blmovie.core.model.PlayEpisode
import kotlinx.coroutines.delay
import kotlinx.coroutines.guava.await
import kotlin.math.abs
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import androidx.annotation.OptIn
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.res.painterResource
import androidx.media3.common.util.UnstableApi
import com.balon.blmovie.R
import com.quick.app.core.extension.clickableNoRipple

private const val SEEK_STEP_MS = 30_000L
private const val CONTROLS_AUTO_HIDE_TIME_MS = 3_000L
private const val SEEK_HINT_SHOW_TIME_MS = 1_000L

/**
 * 网络视频前向缓冲。
 *
 * 目标是在网络条件允许时维持约 30 到 60 秒的前向缓冲。
 * HLS 分片长度、码率和服务端策略会影响实际缓冲时长，所以不能保证精确到 60 秒。
 */
private const val MIN_BUFFER_MS = 20_000
private const val MAX_BUFFER_MS = 60_000

/**
 * 首次播放只要求约 300ms 的媒体数据即可起播。
 * 这是偏激进的低延迟配置，网络不稳定时更容易发生二次缓冲。
 */
private const val BUFFER_FOR_PLAYBACK_MS = 300
private const val BUFFER_AFTER_REBUFFER_MS = 1_000

/**
 * 横向滑动一整个播放器宽度时，最多调整视频总时长的 50%。
 */
private const val GESTURE_SEEK_RATIO = 0.5f

/**
 * 竖向滑动一整个播放器高度时，亮度或音量从最低调到最高。
 */
private const val VERTICAL_GESTURE_RATIO = 1f

private val PlayerIconButtonSize = 40.dp
private val PlayerIconSize = 25.dp

/**
 * 横屏控制栏统一使用同一套左右边距。
 * 顶部系统信息、顶部操作栏和底部控制栏都对齐到这条边线。
 */
private val FullScreenHorizontalPadding = 12.dp
private val FullScreenSystemInfoTopPadding = 2.dp
private val FullScreenTopBarTopPadding = 22.dp
private val FullScreenBottomPadding = 8.dp

private val PlayerAccentColor = Color(0xFFFF6699)

private enum class SeekHint {
    Backward,
    Forward
}

private enum class PlayerGestureMode {
    None,
    Seek,
    Brightness,
    Volume
}

@OptIn(UnstableApi::class)
@Composable
fun MyVideoPlayer(
    url: String,
    modifier: Modifier = Modifier,

    /**
     * 视频首帧出来前显示的封面。
     * 传电影海报或视频封面，可以避免进入详情页时长时间黑屏。
     */
    posterUrl: String? = null,

    title: String? = null,
    episodes: List<PlayEpisode> = emptyList(),
    currentEpisode: PlayEpisode? = null,
    onEpisodeSelected: (PlayEpisode) -> Unit = {},
    qualities: List<String> = listOf("自动", "高清", "超清"),
    currentQuality: String = "自动",
    onQualitySelected: (String) -> Unit = {},
    isFullScreen: Boolean = false,
    isFullScreenChanging: Boolean = false,
    isLeavingPage: Boolean = false,
    onFullScreenClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},

    /**
     * 全屏底部弹幕输入区域的点击回调。
     * 后面接入弹幕输入框时，从外层传入即可。
     */
    onDanmakuClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val appContext = context.applicationContext
    val activity = remember(context) {
        context.findActivity()
    }
    val audioManager = remember(appContext) {
        appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val hapticFeedback = LocalHapticFeedback.current

    /**
     * ExoPlayer 在当前详情页面中只创建一次。
     * 横竖屏切换只改变布局，不重新创建播放器。
     */
    val exoPlayer = remember {
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                MIN_BUFFER_MS,
                MAX_BUFFER_MS,
                BUFFER_FOR_PLAYBACK_MS,
                BUFFER_AFTER_REBUFFER_MS
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        ExoPlayer.Builder(appContext)
            .setLoadControl(loadControl)
            .build()
    }

    /**
     * 拖动进度条时抽取目标时间点的视频帧。
     *
     * EXACT 会解码到用户当前拖动的目标位置，预览画面更准确。
     * 网络 HLS 的精确抽帧成本较高，因此下面会做很短的请求节流。
     */
    val frameExtractor = remember(url) {
        if (url.isBlank()) {
            null
        } else {
            runCatching {
                FrameExtractor.Builder(
                    appContext,
                    MediaItem.fromUri(url)
                )
                    .setSeekParameters(SeekParameters.EXACT)
                    .build()
            }.getOrNull()
        }
    }

    DisposableEffect(frameExtractor) {
        onDispose {
            frameExtractor?.close()
        }
    }

    var controlsVisible by remember {
        mutableStateOf(false)
    }

    var isMenuExpanded by remember {
        mutableStateOf(false)
    }

    var isPlaying by remember {
        mutableStateOf(false)
    }

    var isEnded by remember {
        mutableStateOf(false)
    }

    var isBuffering by remember {
        mutableStateOf(false)
    }

    var currentPosition by remember {
        mutableLongStateOf(0L)
    }

    var duration by remember {
        mutableLongStateOf(0L)
    }

    /**
     * 已经缓冲到的位置，用于绘制进度条的缓冲层。
     */
    var bufferedPosition by remember {
        mutableLongStateOf(0L)
    }

    var seekHint by remember {
        mutableStateOf<SeekHint?>(null)
    }

    var isDraggingProgress by remember {
        mutableStateOf(false)
    }

    var previewPosition by remember {
        mutableLongStateOf(0L)
    }

    /**
     * 进度条预览帧。
     * 每 1 秒取一个预览采样点，避免拖动时连续解码过多帧。
     */
    var previewFrameBitmap by remember(url) {
        mutableStateOf<Bitmap?>(null)
    }

    var isPreviewFrameLoading by remember {
        mutableStateOf(false)
    }

    /**
     * 首帧是否已经真正渲染。
     * 首帧出来前可以继续显示 posterUrl，避免黑屏。
     */
    var hasRenderedFirstFrame by remember(url) {
        mutableStateOf(false)
    }

    /**
     * 缓冲提示延迟显示。
     * 短暂缓冲不会闪一下转圈，超过 300ms 才显示。
     */
    var showBufferingIndicator by remember {
        mutableStateOf(false)
    }

    /**
     * 用户在倍速菜单中选择的常驻倍速。
     */
    var speed by remember {
        mutableFloatStateOf(1f)
    }

    /**
     * 长按临时 2 倍速相关状态。
     * 松手以后恢复长按前的倍速。
     */
    var isLongPressSpeed by remember {
        mutableStateOf(false)
    }

    var speedBeforeLongPress by remember {
        mutableFloatStateOf(1f)
    }

    /**
     * 横向快进、左侧亮度、右侧音量共用的一套手势状态。
     */
    var gestureMode by remember {
        mutableStateOf(PlayerGestureMode.None)
    }

    var gestureSeekPosition by remember {
        mutableLongStateOf(0L)
    }

    var gestureSeekHint by remember {
        mutableStateOf<SeekHint?>(null)
    }

    var gestureLevel by remember {
        mutableFloatStateOf(0f)
    }

    /**
     * 保存进入播放器前的窗口亮度。
     * 离开页面时恢复，避免详情页调亮度影响整个 App。
     */
    val originalWindowBrightness = remember(activity) {
        activity?.window?.attributes?.screenBrightness ?: -1f
    }

    DisposableEffect(activity) {
        activity?.volumeControlStream = AudioManager.STREAM_MUSIC

        onDispose {
            activity?.let { currentActivity ->
                val attributes = currentActivity.window.attributes
                attributes.screenBrightness = originalWindowBrightness
                currentActivity.window.attributes = attributes
            }
        }
    }

    /**
     * A 详情进入 B 详情时暂停 A。
     * 从 B 返回 A 时，如果 A 原来正在播放，则继续原进度播放。
     */
    DisposableEffect(lifecycleOwner, exoPlayer) {
        var shouldResumeWhenActive = false
        var pausedByLifecycle = false

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE,
                Lifecycle.Event.ON_STOP -> {
                    if (!pausedByLifecycle) {
                        shouldResumeWhenActive =
                            exoPlayer.isPlaying || exoPlayer.playWhenReady
                        pausedByLifecycle = true
                    }
                    exoPlayer.pause()
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (
                        pausedByLifecycle &&
                        shouldResumeWhenActive &&
                        exoPlayer.playbackState != Player.STATE_ENDED
                    ) {
                        exoPlayer.play()
                    }

                    pausedByLifecycle = false
                    shouldResumeWhenActive = false
                }

                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    /**
     * 横竖屏切换时控制栏隐藏。
     * 切换完成后也不自动显示，用户再次单击视频才显示。
     */
    LaunchedEffect(isFullScreenChanging) {
        if (isFullScreenChanging) {
            controlsVisible = false
            isMenuExpanded = false
        }
    }

    LaunchedEffect(isLeavingPage) {
        if (isLeavingPage) {
            exoPlayer.pause()
            exoPlayer.clearVideoSurface()
        }
    }

    /**
     * 播放地址真正变化时才切换媒体源。
     */
    LaunchedEffect(url) {
        if (url.isBlank()) return@LaunchedEffect

        val currentUrl = exoPlayer.currentMediaItem
            ?.localConfiguration
            ?.uri
            ?.toString()

        if (currentUrl == url) return@LaunchedEffect

        isBuffering = true
        controlsVisible = false
        hasRenderedFirstFrame = false
        previewFrameBitmap = null

        exoPlayer.setMediaItem(MediaItem.fromUri(url))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    /**
     * 常驻倍速发生变化时同步给播放器。
     * 长按 2 倍速期间不覆盖临时倍速。
     */
    LaunchedEffect(speed, isLongPressSpeed) {
        if (!isLongPressSpeed) {
            exoPlayer.setPlaybackSpeed(speed)
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
                isEnded = playbackState == Player.STATE_ENDED

                if (playbackState == Player.STATE_ENDED) {
                    val safeDuration = exoPlayer.duration
                        .takeIf { it > 0L }
                        ?: duration

                    if (safeDuration > 0L) {
                        currentPosition = safeDuration
                        duration = safeDuration
                    }
                }
            }

            override fun onIsPlayingChanged(isPlayingValue: Boolean) {
                isPlaying = isPlayingValue
            }

            override fun onRenderedFirstFrame() {
                hasRenderedFirstFrame = true
            }
        }

        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.release()
        }
    }

    /**
     * 定时同步播放器进度。
     */
    LaunchedEffect(exoPlayer) {
        while (true) {
            val safeDuration = exoPlayer.duration.takeIf { it > 0L } ?: 0L

            isPlaying = exoPlayer.isPlaying
            isEnded = exoPlayer.playbackState == Player.STATE_ENDED
            duration = safeDuration
            bufferedPosition = if (safeDuration > 0L) {
                exoPlayer.bufferedPosition.coerceIn(0L, safeDuration)
            } else {
                exoPlayer.bufferedPosition.coerceAtLeast(0L)
            }

            currentPosition = if (safeDuration > 0L) {
                exoPlayer.currentPosition.coerceIn(0L, safeDuration)
            } else {
                exoPlayer.currentPosition.coerceAtLeast(0L)
            }

            delay(500L)
        }
    }


    /**
     * 缓冲超过 300ms 才显示转圈，减少刚进入页面时的闪烁感。
     */
    LaunchedEffect(isBuffering) {
        if (isBuffering) {
            delay(300L)
            showBufferingIndicator = true
        } else {
            showBufferingIndicator = false
        }
    }

    /**
     * 拖动进度条时提取目标时间点的实际视频画面。
     *
     * 不再按整秒取整，直接使用当前拖动位置。
     * 仅延迟 45ms 合并过密请求，手指快速移动时旧任务会被取消。
     */
    val previewFrameRequestPosition = if (isDraggingProgress) {
        previewPosition.coerceIn(0L, duration.coerceAtLeast(1L))
    } else {
        -1L
    }

    LaunchedEffect(
        frameExtractor,
        previewFrameRequestPosition
    ) {
        if (
            frameExtractor == null ||
            previewFrameRequestPosition < 0L
        ) {
            isPreviewFrameLoading = false
            return@LaunchedEffect
        }

        delay(45L)
        isPreviewFrameLoading = true

        runCatching {
            frameExtractor
                .getFrame(previewFrameRequestPosition)
                .await()
                .bitmap
        }.onSuccess { bitmap ->
            previewFrameBitmap = bitmap
        }

        isPreviewFrameLoading = false
    }

    /**
     * 控制栏自动隐藏。
     * 拖进度、打开菜单、横竖屏切换、长按倍速、调亮度音量时不计时。
     */
    LaunchedEffect(
        controlsVisible,
        isDraggingProgress,
        isBuffering,
        isMenuExpanded,
        isFullScreenChanging,
        isLongPressSpeed,
        gestureMode
    ) {
        if (
            controlsVisible &&
            !isDraggingProgress &&
            !isBuffering &&
            !isMenuExpanded &&
            !isFullScreenChanging &&
            !isLongPressSpeed &&
            gestureMode == PlayerGestureMode.None
        ) {
            delay(CONTROLS_AUTO_HIDE_TIME_MS)

            if (
                controlsVisible &&
                !isDraggingProgress &&
                !isBuffering &&
                !isMenuExpanded &&
                !isFullScreenChanging &&
                !isLongPressSpeed &&
                gestureMode == PlayerGestureMode.None
            ) {
                controlsVisible = false
            }
        }
    }

    LaunchedEffect(seekHint) {
        if (seekHint != null) {
            delay(SEEK_HINT_SHOW_TIME_MS)
            seekHint = null
        }
    }

    fun playPauseOrReplay() {
        if (exoPlayer.playbackState == Player.STATE_ENDED || isEnded) {
            exoPlayer.seekTo(0L)
            exoPlayer.playWhenReady = true
            exoPlayer.play()

            currentPosition = 0L
            isEnded = false
            isPlaying = true
            controlsVisible = true
            return
        }

        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }

        controlsVisible = true
    }

    Box(
        modifier = modifier
            .background(Color.Black)

            /**
             * 一套拖动识别同时处理三种操作。
             *
             * 水平拖动：快进和快退
             * 全屏左半边垂直拖动：亮度
             * 全屏右半边垂直拖动：媒体音量
             *
             * 先根据手指主要移动方向决定模式，避免水平和垂直手势互相抢事件。
             */
            .pointerInput(duration, isFullScreen, isFullScreenChanging) {
                var gestureEnabled = false
                var startX = 0f
                var totalDragX = 0f
                var totalDragY = 0f
                var startPosition = 0L
                var startBrightness = 0.5f
                var startVolume = 0

                val maxVolume = audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    .coerceAtLeast(1)

                detectDragGestures(
                    onDragStart = { offset ->
                        val bottomControlAreaPx = if (isFullScreen) {
                            116.dp.toPx()
                        } else {
                            72.dp.toPx()
                        }

                        gestureEnabled =
                            !isFullScreenChanging &&
                                    offset.y < size.height - bottomControlAreaPx

                        if (!gestureEnabled) return@detectDragGestures

                        startX = offset.x
                        totalDragX = 0f
                        totalDragY = 0f
                        gestureMode = PlayerGestureMode.None

                        val safeDuration = exoPlayer.duration
                            .takeIf { it > 0L }
                            ?: duration

                        startPosition = exoPlayer.currentPosition.coerceAtLeast(0L)
                        gestureSeekPosition = startPosition.coerceAtMost(
                            safeDuration.coerceAtLeast(startPosition)
                        )
                        gestureSeekHint = null

                        startBrightness = activity
                            ?.currentEffectiveBrightness()
                            ?: 0.5f

                        startVolume = audioManager.getStreamVolume(
                            AudioManager.STREAM_MUSIC
                        )
                    },
                    onDrag = { change, dragAmount ->
                        if (!gestureEnabled) return@detectDragGestures

                        totalDragX += dragAmount.x
                        totalDragY += dragAmount.y

                        if (gestureMode == PlayerGestureMode.None) {
                            val movedEnough =
                                abs(totalDragX) > viewConfiguration.touchSlop ||
                                        abs(totalDragY) > viewConfiguration.touchSlop

                            if (!movedEnough) return@detectDragGestures

                            gestureMode = if (abs(totalDragX) >= abs(totalDragY)) {
                                PlayerGestureMode.Seek
                            } else if (isFullScreen) {
                                if (startX < size.width / 2f) {
                                    PlayerGestureMode.Brightness
                                } else {
                                    PlayerGestureMode.Volume
                                }
                            } else {
                                // 竖屏下不接管垂直滑动，避免影响详情页滚动。
                                PlayerGestureMode.None
                            }

                            if (gestureMode != PlayerGestureMode.None) {
                                controlsVisible = false
                                isMenuExpanded = false
                            }
                        }

                        when (gestureMode) {
                            PlayerGestureMode.Seek -> {
                                val safeDuration = (
                                        exoPlayer.duration.takeIf { it > 0L }
                                            ?: duration
                                        ).coerceAtLeast(1L)

                                val width = size.width.coerceAtLeast(1).toFloat()
                                val deltaMs = (
                                        totalDragX / width *
                                                safeDuration *
                                                GESTURE_SEEK_RATIO
                                        ).toLong()

                                val target = (startPosition + deltaMs)
                                    .coerceIn(0L, safeDuration)

                                gestureSeekPosition = target
                                gestureSeekHint = when {
                                    target > startPosition -> SeekHint.Forward
                                    target < startPosition -> SeekHint.Backward
                                    else -> null
                                }
                            }

                            PlayerGestureMode.Brightness -> {
                                val height = size.height.coerceAtLeast(1).toFloat()
                                val delta = -totalDragY / height *
                                        VERTICAL_GESTURE_RATIO

                                val newBrightness = (startBrightness + delta)
                                    .coerceIn(0.02f, 1f)

                                activity?.setPlayerBrightness(newBrightness)
                                gestureLevel = newBrightness
                            }

                            PlayerGestureMode.Volume -> {
                                val height = size.height.coerceAtLeast(1).toFloat()
                                val delta = -totalDragY / height *
                                        maxVolume *
                                        VERTICAL_GESTURE_RATIO

                                val newVolume = (startVolume + delta)
                                    .roundToInt()
                                    .coerceIn(0, maxVolume)

                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    newVolume,
                                    0
                                )

                                gestureLevel =
                                    newVolume.toFloat() / maxVolume.toFloat()
                            }

                            PlayerGestureMode.None -> Unit
                        }

                        if (gestureMode != PlayerGestureMode.None) {
                            change.consume()
                        }
                    },
                    onDragEnd = {
                        if (gestureMode == PlayerGestureMode.Seek) {
                            exoPlayer.seekTo(gestureSeekPosition)
                            currentPosition = gestureSeekPosition
                            isEnded = false
                        }

                        gestureEnabled = false
                        gestureMode = PlayerGestureMode.None
                        gestureSeekHint = null
                    },
                    onDragCancel = {
                        gestureEnabled = false
                        gestureMode = PlayerGestureMode.None
                        gestureSeekHint = null
                    }
                )
            }

            /**
             * 单击、双击和长按。
             *
             * 单击：显示或隐藏控制栏
             * 双击左侧：快退 30 秒
             * 双击中间：播放或暂停
             * 双击右侧：快进 30 秒
             * 长按：临时 2 倍速，松手恢复原倍速
             */
            .pointerInput(isFullScreenChanging, speed) {
                detectTapGestures(
                    onPress = {
                        try {
                            tryAwaitRelease()
                        } finally {
                            if (isLongPressSpeed) {
                                exoPlayer.setPlaybackSpeed(speedBeforeLongPress)
                                isLongPressSpeed = false
                            }
                        }
                    },
                    onLongPress = {
                        if (
                            !isFullScreenChanging &&
                            exoPlayer.playbackState != Player.STATE_ENDED
                        ) {
                            speedBeforeLongPress =
                                exoPlayer.playbackParameters.speed

                            exoPlayer.setPlaybackSpeed(2f)
                            isLongPressSpeed = true
                            controlsVisible = false
                            isMenuExpanded = false

                            hapticFeedback.performHapticFeedback(
                                HapticFeedbackType.LongPress
                            )
                        }
                    },
                    onTap = {
                        if (
                            isFullScreenChanging ||
                            isLongPressSpeed ||
                            gestureMode != PlayerGestureMode.None
                        ) {
                            return@detectTapGestures
                        }

                        controlsVisible = !controlsVisible
                    },
                    onDoubleTap = { offset: Offset ->
                        if (isFullScreenChanging || isLongPressSpeed) {
                            return@detectTapGestures
                        }

                        val partWidth = size.width / 3f

                        when {
                            offset.x < partWidth -> {
                                val target =
                                    (exoPlayer.currentPosition - SEEK_STEP_MS)
                                        .coerceAtLeast(0L)

                                exoPlayer.seekTo(target)
                                isEnded = false
                                seekHint = SeekHint.Backward
                                controlsVisible = true
                            }

                            offset.x > partWidth * 2f -> {
                                val safeDuration = exoPlayer.duration
                                    .takeIf { it > 0L }
                                    ?: Long.MAX_VALUE

                                val target =
                                    (exoPlayer.currentPosition + SEEK_STEP_MS)
                                        .coerceAtMost(safeDuration)

                                exoPlayer.seekTo(target)
                                isEnded = false
                                seekHint = SeekHint.Forward
                                controlsVisible = true
                            }

                            else -> {
                                playPauseOrReplay()
                            }
                        }
                    }
                )
            }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { viewContext ->
                PlayerView(viewContext).apply {
                    player = exoPlayer
                    useController = false

                    // 首帧之前不要额外盖一层黑色 shutter。
                    setShutterBackgroundColor(android.graphics.Color.TRANSPARENT)
                    setKeepContentOnPlayerReset(true)
                }
            },
            update = { playerView ->
                if (playerView.player !== exoPlayer) {
                    playerView.player = exoPlayer
                }

                /**
                 * 视频正在播放且未结束时保持亮屏。
                 * 暂停、播放结束或 View 离开页面后，系统可以正常息屏。
                 */
                playerView.keepScreenOn = isPlaying && !isEnded
            },
            onRelease = { playerView ->
                playerView.player = null
            }
        )

        /**
         * 首帧出来前显示封面，避免进入详情页时只看到黑屏。
         * 外层调用时传入 posterUrl 才会显示。
         */
        if (
            !hasRenderedFirstFrame &&
            !posterUrl.isNullOrBlank()
        ) {
            AsyncImage(
                model = posterUrl,
                contentDescription = "视频封面",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (showBufferingIndicator) {
            PlayerLoadingOverlay()
        }

        /**
         * 暂停时在画面中央显示暂停图标。
         * 缓冲、播放结束和尚未拿到时长时不显示。
         */
        PauseCenterOverlay(
            visible =
                !isPlaying &&
                        !isBuffering &&
                        !isEnded &&
                        duration > 0L
        )

        SeekHintOverlay(seekHint = seekHint)

        GestureSeekOverlay(
            isVisible = gestureMode == PlayerGestureMode.Seek,
            seekHint = gestureSeekHint,
            previewPosition = gestureSeekPosition,
            duration = duration
        )

        VerticalGestureOverlay(
            mode = gestureMode,
            level = gestureLevel
        )

        LongPressSpeedOverlay(
            visible = isLongPressSpeed
        )

        /**
         * 竖屏控制栏隐藏后，视频最底部保留一条极细的播放状态进度。
         * 不显示时间和拖动圆点，也不会拦截触摸。
         */
        if (
            !isFullScreen &&
            !controlsVisible &&
            !isFullScreenChanging &&
            duration > 0L
        ) {
            PortraitHiddenProgressBar(
                currentPosition = currentPosition,
                bufferedPosition = bufferedPosition,
                duration = duration,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        AnimatedVisibility(
            visible =
                controlsVisible &&
                        !isFullScreenChanging &&
                        !isLongPressSpeed &&
                        gestureMode == PlayerGestureMode.None,
            modifier = Modifier.fillMaxSize()
        ) {
            PlayerControlsOverlay(
                title = title,
                isPlaying = isPlaying,
                currentPosition = currentPosition,
                duration = duration,
                bufferedPosition = bufferedPosition,
                isDraggingProgress = isDraggingProgress,
                previewPosition = previewPosition,
                previewFrameBitmap = previewFrameBitmap,
                isPreviewFrameLoading = isPreviewFrameLoading,
                speed = speed,
                episodes = episodes,
                currentEpisode = currentEpisode,
                qualities = qualities,
                currentQuality = currentQuality,
                isFullScreen = isFullScreen,
                onBackClick = onBackClick,
                onHomeClick = onHomeClick,
                onMoreClick = onMoreClick,
                onDanmakuClick = onDanmakuClick,
                onPlayPauseClick = {
                    playPauseOrReplay()
                },
                onSliderValueChange = { value ->
                    isDraggingProgress = true
                    previewPosition = value.toLong()
                        .coerceIn(0L, duration.coerceAtLeast(1L))
                },
                onSliderValueChangeFinished = {
                    exoPlayer.seekTo(previewPosition)
                    isEnded = false
                    isDraggingProgress = false
                    controlsVisible = true
                },
                onSpeedSelected = { newSpeed ->
                    speed = newSpeed
                    controlsVisible = true
                },
                onEpisodeSelected = { episode ->
                    onEpisodeSelected(episode)
                    controlsVisible = true
                },
                onQualitySelected = { quality ->
                    onQualitySelected(quality)
                    controlsVisible = true
                },
                onFullScreenClick = {
                    controlsVisible = false
                    isMenuExpanded = false
                    onFullScreenClick()
                },
                onMenuExpandedChange = { expanded ->
                    isMenuExpanded = expanded
                    controlsVisible = true
                }
            )
        }
    }
}

/**
 * 播放器控制层总布局。
 *
 * 顶部区域：
 * 返回、主页或标题、扩展操作、更多。
 *
 * 底部区域：
 * 竖屏使用紧凑单行控制栏。
 * 横屏使用接近常见视频应用的两层控制栏。
 */
@Composable
private fun PlayerControlsOverlay(
    title: String?,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    bufferedPosition: Long,
    isDraggingProgress: Boolean,
    previewPosition: Long,
    previewFrameBitmap: Bitmap?,
    isPreviewFrameLoading: Boolean,
    speed: Float,
    episodes: List<PlayEpisode>,
    currentEpisode: PlayEpisode?,
    qualities: List<String>,
    currentQuality: String,
    isFullScreen: Boolean,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onMoreClick: () -> Unit,
    onDanmakuClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSliderValueChange: (Float) -> Unit,
    onSliderValueChangeFinished: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    onEpisodeSelected: (PlayEpisode) -> Unit,
    onQualitySelected: (String) -> Unit,
    onFullScreenClick: () -> Unit,
    onMenuExpandedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0f to Color.Black.copy(alpha = 0.62f),
                        0.24f to Color.Transparent,
                        0.62f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.82f)
                    )
                )
            )
    ) {
        if (isFullScreen) {
            FullScreenSystemInfoBar(
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        TopPlayerBar(
            title = title,
            isFullScreen = isFullScreen,
            onBackClick = onBackClick,
            onHomeClick = onHomeClick,
            onMoreClick = onMoreClick,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        if (isFullScreen) {
            FullScreenBottomControls(
                isPlaying = isPlaying,
                currentPosition = if (isDraggingProgress) {
                    previewPosition
                } else {
                    currentPosition
                },
                duration = duration,
                bufferedPosition = bufferedPosition,
                isDraggingProgress = isDraggingProgress,
                previewPosition = previewPosition,
                previewFrameBitmap = previewFrameBitmap,
                isPreviewFrameLoading = isPreviewFrameLoading,
                speed = speed,
                episodes = episodes,
                currentEpisode = currentEpisode,
                qualities = qualities,
                currentQuality = currentQuality,
                onPlayPauseClick = onPlayPauseClick,
                onSliderValueChange = onSliderValueChange,
                onSliderValueChangeFinished = onSliderValueChangeFinished,
                onSpeedSelected = onSpeedSelected,
                onEpisodeSelected = onEpisodeSelected,
                onQualitySelected = onQualitySelected,
                onDanmakuClick = onDanmakuClick,
                onFullScreenClick = onFullScreenClick,
                onMenuExpandedChange = onMenuExpandedChange,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        } else {
            PortraitBottomControls(
                isPlaying = isPlaying,
                currentPosition = if (isDraggingProgress) {
                    previewPosition
                } else {
                    currentPosition
                },
                duration = duration,
                bufferedPosition = bufferedPosition,
                isDraggingProgress = isDraggingProgress,
                previewPosition = previewPosition,
                previewFrameBitmap = previewFrameBitmap,
                isPreviewFrameLoading = isPreviewFrameLoading,
                onPlayPauseClick = onPlayPauseClick,
                onSliderValueChange = onSliderValueChange,
                onSliderValueChangeFinished = onSliderValueChangeFinished,
                onFullScreenClick = onFullScreenClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}


/**
 * 横屏顶部的系统信息区域。
 *
 * 中间显示当前时间，右侧显示当前电量。
 * 它属于播放器自己的 UI，不依赖系统状态栏是否显示。
 */
@Composable
private fun FullScreenSystemInfoBar(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var timeText by remember {
        mutableStateOf(currentClockText())
    }

    var batteryLevel by remember(context) {
        mutableStateOf(context.readBatteryLevel())
    }

    LaunchedEffect(context) {
        while (true) {
            timeText = currentClockText()
            batteryLevel = context.readBatteryLevel()
            delay(30_000L)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = FullScreenHorizontalPadding,
                end = FullScreenHorizontalPadding,
                top = FullScreenSystemInfoTopPadding
            )
    ) {
        Text(
            text = timeText,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        BatteryIndicator(
            level = batteryLevel,
            modifier = Modifier.align(Alignment.TopEnd)
        )
    }
}

/**
 * 自绘电量图标，避免额外依赖特定的 Material 电池图标。
 */
@Composable
private fun BatteryIndicator(
    level: Int,
    modifier: Modifier = Modifier
) {
    val safeLevel = level.coerceIn(0, 100)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$safeLevel%",
            color = Color.White,
            fontWeight = FontWeight.Medium
        )

        Row(
            modifier = Modifier.padding(start = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(12.dp)
                    .border(
                        width = 1.4.dp,
                        color = Color.White,
                        shape = RoundedCornerShape(3.dp)
                    )
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(safeLevel / 100f)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(1.dp)
                        )
                )
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(6.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(
                            topEnd = 1.dp,
                            bottomEnd = 1.dp
                        )
                    )
            )
        }
    }
}

/**
 * 顶部控制栏。
 *
 * 竖屏：
 * 返回、主页、右侧更多。
 *
 * 横屏：
 * 返回、视频标题、右侧扩展区、更多。
 *
 * 后面要增加点赞、投屏、锁定等功能时，可以放到“横屏扩展区”。
 */
@Composable
private fun TopPlayerBar(
    title: String?,
    isFullScreen: Boolean,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (isFullScreen) {
                    FullScreenHorizontalPadding
                } else {
                    8.dp
                },
                end = if (isFullScreen) {
                    FullScreenHorizontalPadding
                } else {
                    8.dp
                },
                top = if (isFullScreen) {
                    FullScreenTopBarTopPadding
                } else {
                    3.dp
                },
                bottom = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerIconButton(
            onClick = onBackClick,
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "返回",
            contentAlignment = Alignment.CenterStart
        )

        if (!isFullScreen) {
            PlayerIconButton(
                onClick = onHomeClick,
                imageVector = Icons.Default.Home,
                contentDescription = "主页",
                contentAlignment = Alignment.CenterStart
            )
        }

        if (isFullScreen) {
            Text(
                text = title.orEmpty(),
                color = Color.White,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )

            /**
             * 横屏顶部扩展区。
             * 后续可在这里添加点赞、收藏、投屏、锁屏等按钮。
             */
//            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        PlayerIconButton(
            onClick = onMoreClick,
            imageVector = Icons.Default.MoreVert,
            contentDescription = "更多",
            contentAlignment = Alignment.CenterEnd,
        )
    }
}

/**
 * 竖屏底部控制栏。
 *
 * 布局：
 * 播放 | 当前时间 | 进度条 | 总时长 | 全屏
 */
@Composable
private fun PortraitBottomControls(
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    bufferedPosition: Long,
    isDraggingProgress: Boolean,
    previewPosition: Long,
    previewFrameBitmap: Bitmap?,
    isPreviewFrameLoading: Boolean,
    onPlayPauseClick: () -> Unit,
    onSliderValueChange: (Float) -> Unit,
    onSliderValueChangeFinished: () -> Unit,
    onFullScreenClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerIconButton(
            onClick = onPlayPauseClick,
            imageVector = if (isPlaying) {
                Icons.Default.Pause
            } else {
                Icons.Default.PlayArrow
            },
            contentDescription = "播放暂停"
        )

        Text(
            text = formatPlayerTime(currentPosition),
            color = Color.White
        )

        PlayerProgressBar(
            currentPosition = currentPosition,
            duration = duration,
            bufferedPosition = bufferedPosition,
            showPreview = isDraggingProgress,
            previewPosition = previewPosition,
            previewFrameBitmap = previewFrameBitmap,
            isPreviewFrameLoading = isPreviewFrameLoading,
            onValueChange = onSliderValueChange,
            onValueChangeFinished = onSliderValueChangeFinished,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp)
        )

        Text(
            text = formatPlayerTime(duration),
            color = Color.White
        )

        PlayerIconButton(
            onClick = onFullScreenClick,
            imageVector = Icons.Default.Fullscreen,
            contentDescription = "进入全屏"
        )
    }
}

/**
 * 横屏底部控制栏。
 *
 * 第一层：
 * 当前时间 / 总时长 + 整行进度条。
 *
 * 第二层：
 * 播放按钮 | 弹幕输入扩展位 | 选集 | 清晰度 | 倍速 | 退出全屏。
 *
 * 后续增加字幕、弹幕开关、下一集等功能时，优先放在第二层。
 */
@Composable
private fun FullScreenBottomControls(
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    bufferedPosition: Long,
    isDraggingProgress: Boolean,
    previewPosition: Long,
    previewFrameBitmap: Bitmap?,
    isPreviewFrameLoading: Boolean,
    speed: Float,
    episodes: List<PlayEpisode>,
    currentEpisode: PlayEpisode?,
    qualities: List<String>,
    currentQuality: String,
    onPlayPauseClick: () -> Unit,
    onSliderValueChange: (Float) -> Unit,
    onSliderValueChangeFinished: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    onEpisodeSelected: (PlayEpisode) -> Unit,
    onQualitySelected: (String) -> Unit,
    onDanmakuClick: () -> Unit,
    onFullScreenClick: () -> Unit,
    onMenuExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = FullScreenHorizontalPadding,
                end = FullScreenHorizontalPadding,
                bottom = FullScreenBottomPadding
            )
    ) {
        Text(
            text = "${formatPlayerTime(currentPosition)}/${formatPlayerTime(duration)}",
            color = Color.White,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp)
        )

        PlayerProgressBar(
            currentPosition = currentPosition,
            duration = duration,
            bufferedPosition = bufferedPosition,
            showPreview = isDraggingProgress,
            previewPosition = previewPosition,
            previewFrameBitmap = previewFrameBitmap,
            isPreviewFrameLoading = isPreviewFrameLoading,
            onValueChange = onSliderValueChange,
            onValueChangeFinished = onSliderValueChangeFinished,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp, horizontal = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerIconButton(
                onClick = onPlayPauseClick,
                imageVector = if (isPlaying) {
                    Icons.Default.Pause
                } else {
                    Icons.Default.PlayArrow
                },
                contentDescription = "播放暂停",
                buttonSize = 30.dp,
                iconSize = 30.dp,
                contentAlignment = Alignment.CenterStart
            )

            /**
             * 弹幕输入扩展位。
             * 目前只抛出点击事件，输入框和发送逻辑放到页面层实现。
             */
            Box(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
                    .height(36.dp)
                    .background(
                        Color.White.copy(alpha = 0.84f),
                        RoundedCornerShape(18.dp)
                    )
                    .clickable {
                        onDanmakuClick()
                    }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "发个友善的弹幕见证当下",
                    color = Color.Black.copy(alpha = 0.56f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            PlayerEpisodeButton(
                episodes = episodes,
                currentEpisode = currentEpisode,
                onEpisodeSelected = onEpisodeSelected,
                onMenuExpandedChange = onMenuExpandedChange
            )

            PlayerQualityButton(
                qualities = qualities,
                currentQuality = currentQuality,
                onQualitySelected = onQualitySelected,
                onMenuExpandedChange = onMenuExpandedChange
            )

            PlayerSpeedButton(
                speed = speed,
                onSpeedSelected = onSpeedSelected,
                onMenuExpandedChange = onMenuExpandedChange
            )

            PlayerIconButton(
                onClick = onFullScreenClick,
                imageVector = Icons.Default.FullscreenExit,
                contentDescription = "退出全屏",
                contentAlignment = Alignment.CenterEnd,
                buttonSize = 30.dp,
                iconSize = 30.dp,
            )
        }
    }
}

@Composable
private fun PlayerIconButton(
    onClick: () -> Unit,
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    buttonSize: Dp = PlayerIconButtonSize,
    iconSize: Dp = PlayerIconSize,
    contentAlignment: Alignment = Alignment.Center
) {
    /**
     * 使用固定大小的 Box，而不是 IconButton 的默认居中留白。
     * 最左和最右按钮可以分别贴齐统一边线，同时仍保留 40dp 点击区域。
     */
    Box(
        modifier = Modifier
            .size(buttonSize)
            .clickableNoRipple(onClick = onClick),
        contentAlignment = contentAlignment
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * 自定义进度条。
 * 白色细轨道和白色实心圆形拖动点。
 */
@Composable
private fun PlayerProgressBar(
    currentPosition: Long,
    duration: Long,
    bufferedPosition: Long,
    showPreview: Boolean,
    previewPosition: Long,
    previewFrameBitmap: Bitmap?,
    isPreviewFrameLoading: Boolean,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var barWidthPx by remember {
        mutableStateOf(1)
    }

    val safeDuration = duration.coerceAtLeast(1L)
    val progress = (
            currentPosition.toFloat() / safeDuration.toFloat()
            ).coerceIn(0f, 1f)

    val bufferedProgress = (
            bufferedPosition.toFloat() / safeDuration.toFloat()
            ).coerceIn(progress, 1f)

    fun updateProgressByOffset(x: Float) {
        val newProgress = (x / barWidthPx.toFloat())
            .coerceIn(0f, 1f)

        onValueChange(newProgress * safeDuration.toFloat())
    }

    BoxWithConstraints(
        modifier = modifier
            .height(28.dp)
            .onSizeChanged {
                barWidthPx = it.width.coerceAtLeast(1)
            }
            .pointerInput(safeDuration) {
                detectTapGestures { offset ->
                    updateProgressByOffset(offset.x)
                    onValueChangeFinished()
                }
            }
            .pointerInput(safeDuration) {
                detectDragGestures(
                    onDragStart = {
                        updateProgressByOffset(it.x)
                    },
                    onDragEnd = onValueChangeFinished,
                    onDragCancel = onValueChangeFinished,
                    onDrag = { change, _ ->
                        updateProgressByOffset(change.position.x)
                        change.consume()
                    }
                )
            }
    ) {
        val thumbSize = 10.dp
        val thumbOffset = (maxWidth - thumbSize) * progress

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .align(Alignment.CenterStart)
                .background(
                    Color.White.copy(alpha = 0.35f),
                    RoundedCornerShape(99.dp)
                )
        )

        /**
         * 已缓冲进度。
         * 比未缓冲轨道更亮，但比已播放轨道更暗。
         */
        Box(
            modifier = Modifier
                .fillMaxWidth(bufferedProgress)
                .height(3.dp)
                .align(Alignment.CenterStart)
                .background(
                    Color.White.copy(alpha = 0.58f),
                    RoundedCornerShape(99.dp)
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(3.dp)
                .align(Alignment.CenterStart)
                .background(
                    Color.White,
                    RoundedCornerShape(99.dp)
                )
        )

        /**
         * 拖动时在小圆点正上方显示预览窗口。
         *
         * 窗口只显示目标位置的真实视频画面，不显示进度数字。
         */
//        if (showPreview) {
//            val previewWidth = 144.dp
//            val maxPreviewX = if (maxWidth > previewWidth) {
//                maxWidth - previewWidth
//            } else {
//                0.dp
//            }
//            val previewX = (
//                    maxWidth * progress - previewWidth / 2
//                    ).coerceIn(0.dp, maxPreviewX)
//
//            ProgressPreviewWindow(
//                previewFrameBitmap = previewFrameBitmap,
//                isLoading = isPreviewFrameLoading,
//                modifier = Modifier
//                    .offset(
//                        x = previewX,
//                        // 预览框底部的小三角紧贴拖动圆点。
//                        y = (-78).dp
//                    )
//            )
//        }

        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .align(Alignment.CenterStart)
                .background(Color.White, CircleShape)
        )
    }
}

@Composable
private fun PlayerSpeedButton(
    speed: Float,
    onSpeedSelected: (Float) -> Unit,
    onMenuExpandedChange: (Boolean) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box {
        TextButton(
            onClick = {
                expanded = true
                onMenuExpandedChange(true)
            }
        ) {
            Text(
                text = if (speed == 1f) "倍速" else "${speed}x",
                color = Color.White
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                onMenuExpandedChange(false)
            }
        ) {
            listOf(0.75f, 1f, 1.25f, 1.5f, 2f).forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text("${item}x")
                    },
                    onClick = {
                        expanded = false
                        onMenuExpandedChange(false)
                        onSpeedSelected(item)
                    }
                )
            }
        }
    }
}

@Composable
private fun PlayerQualityButton(
    qualities: List<String>,
    currentQuality: String,
    onQualitySelected: (String) -> Unit,
    onMenuExpandedChange: (Boolean) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box {
        TextButton(
            onClick = {
                expanded = true
                onMenuExpandedChange(true)
            }
        ) {
            Text(
                text = currentQuality,
                color = Color.White
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                onMenuExpandedChange(false)
            }
        ) {
            qualities.forEach { quality ->
                DropdownMenuItem(
                    text = {
                        Text(quality)
                    },
                    onClick = {
                        expanded = false
                        onMenuExpandedChange(false)
                        onQualitySelected(quality)
                    }
                )
            }
        }
    }
}

@Composable
private fun PlayerEpisodeButton(
    episodes: List<PlayEpisode>,
    currentEpisode: PlayEpisode?,
    onEpisodeSelected: (PlayEpisode) -> Unit,
    onMenuExpandedChange: (Boolean) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box {
        TextButton(
            onClick = {
                expanded = true
                onMenuExpandedChange(true)
            },
            enabled = episodes.isNotEmpty()
        ) {
            Text(
                text = currentEpisode?.name ?: "选集",
                color = Color.White
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
                onMenuExpandedChange(false)
            }
        ) {
            episodes.forEach { episode ->
                DropdownMenuItem(
                    text = {
                        Text(episode.name)
                    },
                    onClick = {
                        expanded = false
                        onMenuExpandedChange(false)
                        onEpisodeSelected(episode)
                    }
                )
            }
        }
    }
}


/**
 * 竖屏控制栏隐藏时显示的底部极细进度条。
 *
 * 只展示播放状态，不显示文字、拖动圆点，也不处理点击。
 */
@Composable
private fun PortraitHiddenProgressBar(
    currentPosition: Long,
    bufferedPosition: Long,
    duration: Long,
    modifier: Modifier = Modifier
) {
    val safeDuration = duration.coerceAtLeast(1L)
    val playedFraction = (
            currentPosition.toFloat() / safeDuration.toFloat()
            ).coerceIn(0f, 1f)

    val bufferedFraction = (
            bufferedPosition.toFloat() / safeDuration.toFloat()
            ).coerceIn(playedFraction, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(2.dp)
            .background(Color.White.copy(alpha = 0.22f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(bufferedFraction)
                .background(Color.White.copy(alpha = 0.42f))
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(playedFraction)
                .background(PlayerAccentColor)
        )
    }
}

/**
 * 进度条拖动预览窗口。
 *
 * 使用 Media3 FrameExtractor 精确解码目标时间点的画面。
 * 如果某个网络视频源不支持快速随机访问，窗口会暂时显示黑色占位。
 */
@Composable
private fun ProgressPreviewWindow(
    previewFrameBitmap: Bitmap?,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(144.dp)
                .height(81.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.92f),
                    shape = RoundedCornerShape(7.dp)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(7.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            previewFrameBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "进度预览画面",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (isLoading && previewFrameBitmap == null) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        /**
         * 小三角指向当前进度圆点。
         * 预览框不再显示时间数字，只展示目标位置的实际画面。
         */
        Canvas(
            modifier = Modifier
                .width(14.dp)
                .height(7.dp)
        ) {
            val triangle = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width / 2f, size.height)
                close()
            }
            drawPath(
                path = triangle,
                color = Color.Black.copy(alpha = 0.92f)
            )
        }
    }
}

/**
 * 播放暂停时的画面中央提示。
 *
 * 显示时从小到大并逐渐出现。
 * 隐藏时轻微放大并逐渐消失。
 */
@Composable
private fun PauseCenterOverlay(
    visible: Boolean
) {
    AnimatedVisibility(
        visible = visible,
        modifier = Modifier.fillMaxSize(),
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 180
            )
        ) + scaleIn(
            initialScale = 0.65f,
            animationSpec = tween(
                durationMillis = 220,
                easing = FastOutSlowInEasing
            )
        ),
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = 160
            )
        ) + scaleOut(
            targetScale = 1.2f,
            animationSpec = tween(
                durationMillis = 180,
                easing = FastOutSlowInEasing
            )
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(62.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.play),
                    contentDescription = "已暂停",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@Composable
private fun PlayerLoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.08f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White,
            strokeWidth = 2.dp,
            modifier = Modifier.size(28.dp)
        )
    }
}

/**
 * 长按 2 倍速提示，位置接近全屏播放器顶部中间。
 */
@Composable
private fun LongPressSpeedOverlay(
    visible: Boolean
) {
    AnimatedVisibility(
        visible = visible,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 22.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Row(
                modifier = Modifier
                    .background(
                        Color.Black.copy(alpha = 0.58f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = null,
                    tint = Color.White
                )

                Text(
                    text = "2x 倍速播放中",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

/**
 * 左侧亮度、右侧音量调节提示。
 */
@Composable
private fun VerticalGestureOverlay(
    mode: PlayerGestureMode,
    level: Float
) {
    if (
        mode != PlayerGestureMode.Brightness &&
        mode != PlayerGestureMode.Volume
    ) {
        return
    }

    val icon = if (mode == PlayerGestureMode.Brightness) {
        Icons.Default.Brightness6
    } else {
        Icons.Default.VolumeUp
    }

    val description = if (mode == PlayerGestureMode.Brightness) {
        "亮度"
    } else {
        "音量"
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .background(
                    Color.Black.copy(alpha = 0.58f),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 22.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )

            Box(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .width(150.dp)
                    .height(5.dp)
                    .background(
                        Color.White.copy(alpha = 0.28f),
                        RoundedCornerShape(99.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(level.coerceIn(0f, 1f))
                        .background(
                            Color(0xFFFF6699),
                            RoundedCornerShape(99.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun GestureSeekOverlay(
    isVisible: Boolean,
    seekHint: SeekHint?,
    previewPosition: Long,
    duration: Long
) {
    if (!isVisible) return

    val text = when (seekHint) {
        SeekHint.Backward -> "快退"
        SeekHint.Forward -> "快进"
        null -> "调整进度"
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$text  ${formatPlayerTime(previewPosition)} / ${formatPlayerTime(duration)}",
            color = Color.White,
            modifier = Modifier
                .background(
                    Color.Black.copy(alpha = 0.58f),
                    RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 18.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun SeekHintOverlay(
    seekHint: SeekHint?
) {
    if (seekHint == null) return

    val alignment = when (seekHint) {
        SeekHint.Backward -> Alignment.CenterStart
        SeekHint.Forward -> Alignment.CenterEnd
    }

    val icon = when (seekHint) {
        SeekHint.Backward -> Icons.Default.FastRewind
        SeekHint.Forward -> Icons.Default.FastForward
    }

    val text = when (seekHint) {
        SeekHint.Backward -> "快退 30 秒"
        SeekHint.Forward -> "快进 30 秒"
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .align(alignment)
                .padding(horizontal = 42.dp)
                .background(
                    Color.Black.copy(alpha = 0.45f),
                    RoundedCornerShape(32.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.White
            )

            Text(
                text = text,
                color = Color.White,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}


private val PlayerClockFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("HH:mm")

private fun currentClockText(): String {
    return LocalTime.now().format(PlayerClockFormatter)
}

private fun Context.readBatteryLevel(): Int {
    val batteryManager =
        getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    val propertyLevel = batteryManager.getIntProperty(
        BatteryManager.BATTERY_PROPERTY_CAPACITY
    )

    if (propertyLevel in 0..100) {
        return propertyLevel
    }

    val batteryIntent = registerReceiver(
        null,
        IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    )

    val level = batteryIntent?.getIntExtra(
        BatteryManager.EXTRA_LEVEL,
        -1
    ) ?: -1

    val scale = batteryIntent?.getIntExtra(
        BatteryManager.EXTRA_SCALE,
        -1
    ) ?: -1

    return if (level >= 0 && scale > 0) {
        (level * 100f / scale).roundToInt().coerceIn(0, 100)
    } else {
        0
    }
}

private fun Activity.currentEffectiveBrightness(): Float {
    val windowBrightness = window.attributes.screenBrightness

    if (windowBrightness >= 0f) {
        return windowBrightness.coerceIn(0.02f, 1f)
    }

    val systemBrightness = Settings.System.getInt(
        contentResolver,
        Settings.System.SCREEN_BRIGHTNESS,
        128
    )

    return (systemBrightness / 255f).coerceIn(0.02f, 1f)
}

private fun Activity.setPlayerBrightness(value: Float) {
    val attributes = window.attributes
    attributes.screenBrightness = value.coerceIn(0.02f, 1f)
    window.attributes = attributes
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

private fun formatPlayerTime(timeMs: Long): String {
    if (timeMs <= 0L) return "00:00"

    val totalSeconds = timeMs / 1_000L
    val hours = totalSeconds / 3_600L
    val minutes = totalSeconds % 3_600L / 60L
    val seconds = totalSeconds % 60L

    return if (hours > 0L) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}