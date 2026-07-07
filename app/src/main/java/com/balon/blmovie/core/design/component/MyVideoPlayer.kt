package com.balon.blmovie.core.design.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.balon.blmovie.core.design.theme.SpaceSmallWidth
import com.balon.blmovie.core.model.PlayEpisode
import kotlinx.coroutines.delay
import timber.log.Timber
import kotlin.time.Duration.Companion.milliseconds
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

private const val SEEK_STEP_MS = 30_000L
private const val CONTROLS_AUTO_HIDE_TIME = 3_000L
private const val SEEK_HINT_SHOW_TIME = 1000L

/**
 * 横向滑动一整个播放器宽度时，最多快进 / 快退视频总时长的 50%。
 * 这样比直接按 100% 总时长滑动更稳，不容易一下子跳太多。
 */
private const val GESTURE_SEEK_WIDTH_TO_DURATION_RATIO = 0.5f

private enum class SeekHint {
    Backward,
    Forward
}

@Composable
fun MyVideoPlayer(
    url: String,
    modifier: Modifier = Modifier,

    /**
     * 播放器标题。
     * 普通模式不展示，只有全屏模式才展示。
     */
    title: String? = null,

    /**
     * 选集列表。
     * 普通模式不展示，只有全屏模式才展示。
     */
    episodes: List<PlayEpisode> = emptyList(),

    /**
     * 当前播放的集数。
     */
    currentEpisode: PlayEpisode? = null,

    /**
     * 点击选集回调。
     */
    onEpisodeSelected: (PlayEpisode) -> Unit = {},

    /**
     * 清晰度列表。
     * 普通模式不展示，只有全屏模式才展示。
     */
    qualities: List<String> = listOf("自动", "高清", "超清"),

    /**
     * 当前清晰度名称。
     */
    currentQuality: String = "自动",

    /**
     * 点击清晰度回调。
     */
    onQualitySelected: (String) -> Unit = {},

    /**
     * 当前是否全屏。
     * 用这个参数切换普通 UI 和全屏 UI，不要重新创建播放器。
     */
    isFullScreen: Boolean = false,

    /**
     * 是否正在切换全屏。
     *
     * 切换横竖屏过程中，控制栏先隐藏。
     * 等切换完成后，再显示新的控制栏 UI。
     */
    isFullScreenChanging: Boolean = false,

    /**
     * 点击全屏按钮。
     */
    onFullScreenClick: () -> Unit = {},

    /**
     * 点击返回按钮。
     */
    onBackClick: () -> Unit = {},

    /**
     * 点击主页按钮。
     * 具体跳转逻辑放在外层 Route 里处理。
     */
    onHomeClick: () -> Unit = {},

    /**
     * 点击右上角更多按钮。
     */
    onMoreClick: () -> Unit = {},

) {

    val context = LocalContext.current
    val appContext = context.applicationContext

    /**
     * ExoPlayer 只创建一次。
     * url 改变时只切换播放源，不重新 new Player。
     */
    val exoPlayer = remember {
        ExoPlayer.Builder(appContext).build()
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, exoPlayer) {
        var shouldResumeWhenActive = false
        var pausedByLifecycle = false

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE,
                Lifecycle.Event.ON_STOP -> {
                    /**
                     * 第一次进入后台时，记录暂停前是否是播放状态。
                     *
                     * 注意不要每次 ON_STOP 都重新赋值。
                     * 因为 ON_PAUSE 里 pause 后，ON_STOP 再判断 isPlaying 就会变成 false。
                     */
                    if (!pausedByLifecycle) {
                        shouldResumeWhenActive =
                            exoPlayer.isPlaying || exoPlayer.playWhenReady

                        pausedByLifecycle = true
                    }
                    exoPlayer.pause()
                }

                Lifecycle.Event.ON_RESUME -> {
                    /**
                     * 回到当前页面时，如果之前是因为生命周期暂停的，
                     * 并且暂停前确实在播放，就恢复播放。
                     */
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
     * 控制栏是否显示。
     */
    var controlsVisible by remember {
        mutableStateOf(false)
    }

    /**
     * 横竖屏切换过程中，隐藏控制栏。
     * 切换完成后，再显示控制栏。
     */
    LaunchedEffect(isFullScreenChanging) {
        if (isFullScreenChanging) {
            controlsVisible = false
        } else {
            controlsVisible = true
        }
    }

    /**
     * 是否有弹出菜单正在展开。
     *
     * 比如倍速、清晰度、选集。
     * 菜单展开时，控制栏不能自动隐藏。
     */
    var isMenuExpanded by remember {
        mutableStateOf(false)
    }

    /**
     * 是否正在播放。
     */
    var isPlaying by remember {
        mutableStateOf(false)
    }

    /**
     * 是否已经播放结束。
     * 播放结束后再次点击播放按钮，需要先回到 0，再开始播放。
     */
    var isEnded by remember {
        mutableStateOf(false)
    }

    /**
     * 是否正在缓冲。
     */
    var isBuffering by remember {
        mutableStateOf(false)
    }

    /**
     * 当前播放进度。
     */
    var currentPosition by remember {
        mutableLongStateOf(0L)
    }

    /**
     * 视频总时长。
     */
    var duration by remember {
        mutableLongStateOf(0L)
    }

    /**
     * 双击快进 / 快退提示。
     */
    var seekHint by remember {
        mutableStateOf<SeekHint?>(null)
    }

    /**
     * 是否正在横向滑动调整进度。
     */
    var isGestureSeeking by remember {
        mutableStateOf(false)
    }

    /**
     * 横向滑动时预览的目标进度。
     * 手指松开后，播放器会 seek 到这个位置。
     */
    var gestureSeekPosition by remember {
        mutableLongStateOf(0L)
    }

    /**
     * 横向滑动方向，用来显示“快进”或“快退”。
     */
    var gestureSeekHint by remember {
        mutableStateOf<SeekHint?>(null)
    }

    /**
     * 是否正在拖动进度条。
     */
    var isDraggingProgress by remember {
        mutableStateOf(false)
    }

    /**
     * 拖动进度条时预览的时间。
     */
    var previewPosition by remember {
        mutableLongStateOf(0L)
    }

    /**
     * 倍速。
     */
    var speed by remember {
        mutableStateOf(1f)
    }

    /**
     * url 改变时，切换播放源。
     */
    LaunchedEffect(url) {
        if (url.isBlank()) {
            return@LaunchedEffect
        }

        val currentUrl = exoPlayer.currentMediaItem
            ?.localConfiguration
            ?.uri
            ?.toString()

        /**
         * 只有播放地址真的变了，才重新设置播放源。
         *
         * 全屏和非全屏切换时，url 没变，
         * 这里直接 return，不会重新 setMediaItem，
         * 所以不会从头播放。
         */
        if (currentUrl == url) {
            return@LaunchedEffect
        }

        isBuffering = true
        controlsVisible = false

        exoPlayer.setMediaItem(
            MediaItem.fromUri(url)
        )
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    LaunchedEffect(url) {
        if (url.isBlank()) {
            return@LaunchedEffect
        }

        val currentUrl = exoPlayer.currentMediaItem
            ?.localConfiguration
            ?.uri
            ?.toString()

        Timber.d("MyVideoPlayer url = $url")
        Timber.d("MyVideoPlayer currentUrl = $currentUrl")

        if (currentUrl == url) {
            Timber.d("MyVideoPlayer same url, skip setMediaItem")
            return@LaunchedEffect
        }

        Timber.d("MyVideoPlayer new url, setMediaItem")

        exoPlayer.setMediaItem(MediaItem.fromUri(url))
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    /**
     * 倍速改变时，设置给 ExoPlayer。
     */
    LaunchedEffect(speed) {
        exoPlayer.setPlaybackSpeed(speed)
    }

    /**
     * 监听 ExoPlayer 播放状态。
     */
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {

            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = playbackState == Player.STATE_BUFFERING
                isEnded = playbackState == Player.STATE_ENDED

                /**
                 * 结束时把 UI 进度固定到总时长。
                 * 这样不会出现 24:26 / 24:25 这种显示。
                 */
                if (playbackState == Player.STATE_ENDED) {
                    val safeDuration = exoPlayer.duration.takeIf { it > 0 } ?: duration
                    if (safeDuration > 0L) {
                        currentPosition = safeDuration
                        duration = safeDuration
                    }
                }
            }

            override fun onIsPlayingChanged(isPlayingValue: Boolean) {
                isPlaying = isPlayingValue
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
     * 每隔 500ms 同步一次播放进度。
     */
    LaunchedEffect(exoPlayer) {
        while (true) {
            val safeDuration = exoPlayer.duration.takeIf { it > 0 } ?: 0L

            isPlaying = exoPlayer.isPlaying
            isEnded = exoPlayer.playbackState == Player.STATE_ENDED
            duration = safeDuration

            /**
             * 播放结束或极少数流媒体返回异常位置时，把进度限制在 0..duration。
             */
            currentPosition = if (safeDuration > 0L) {
                exoPlayer.currentPosition.coerceIn(0L, safeDuration)
            } else {
                exoPlayer.currentPosition.coerceAtLeast(0L)
            }

            delay(500L)
        }
    }

    /**
     * 控制栏显示后，3 秒自动隐藏。
     * 正在拖动进度条、正在缓冲时，不自动隐藏。
     */
    LaunchedEffect(
        controlsVisible,
        isDraggingProgress,
        isBuffering,
        isMenuExpanded,
        isFullScreenChanging
    ) {
        if (
            controlsVisible &&
            !isDraggingProgress &&
            !isBuffering &&
            !isMenuExpanded &&
            !isFullScreenChanging
        ) {
            delay(CONTROLS_AUTO_HIDE_TIME.milliseconds)

            if (
                controlsVisible &&
                !isDraggingProgress &&
                !isBuffering &&
                !isMenuExpanded &&
                !isFullScreenChanging
            ) {
                controlsVisible = false
            }
        }
    }

    /**
     * 快进 / 快退提示至少显示 1000ms。
     */
    LaunchedEffect(seekHint) {
        if (seekHint != null) {
            delay(SEEK_HINT_SHOW_TIME.milliseconds)
            seekHint = null
        }
    }

    /**
     * 播放 / 暂停 / 播放结束后重播。
     *
     * ExoPlayer 播放结束后，直接 play() 不一定会从头播放。
     * 所以这里先判断 STATE_ENDED，如果结束了，就 seekTo(0) 再 play。
     */
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
            .pointerInput(duration) {
                var canGestureSeek = false
                var gestureStartPosition = 0L
                var gestureTotalDragPx = 0f

                detectHorizontalDragGestures(
                    /**
                     * 横向滑动开始。
                     * 底部控制栏区域不触发这个手势，避免和进度条拖动冲突。
                     */
                    onDragStart = { offset ->
                        val safeDuration = exoPlayer.duration
                            .takeIf { it > 0 }
                            ?: duration

                        val bottomControlAreaPx = 72.dp.toPx()
                        canGestureSeek = safeDuration > 0L &&
                                offset.y < size.height - bottomControlAreaPx

                        if (canGestureSeek) {
                            gestureStartPosition = exoPlayer.currentPosition
                                .coerceIn(0L, safeDuration)
                            gestureSeekPosition = gestureStartPosition
                            gestureTotalDragPx = 0f
                            gestureSeekHint = null
                            isGestureSeeking = true
                            controlsVisible = true
                        }
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        if (!canGestureSeek) {
                            return@detectHorizontalDragGestures
                        }

                        change.consume()
                        gestureTotalDragPx += dragAmount

                        val safeDuration = (exoPlayer.duration
                            .takeIf { it > 0 }
                            ?: duration)
                            .coerceAtLeast(1L)

                        val playerWidth = size.width
                            .coerceAtLeast(1)
                            .toFloat()

                        val deltaMs = (gestureTotalDragPx / playerWidth *
                                safeDuration *
                                GESTURE_SEEK_WIDTH_TO_DURATION_RATIO)
                            .toLong()

                        val target = (gestureStartPosition + deltaMs)
                            .coerceIn(0L, safeDuration)

                        gestureSeekPosition = target
                        gestureSeekHint = when {
                            target > gestureStartPosition -> SeekHint.Forward
                            target < gestureStartPosition -> SeekHint.Backward
                            else -> null
                        }
                    },
                    /**
                     * 手指松开后，真正跳转到预览进度。
                     */
                    onDragEnd = {
                        if (canGestureSeek && isGestureSeeking) {
                            exoPlayer.seekTo(gestureSeekPosition)
                            currentPosition = gestureSeekPosition
                            isEnded = false
                        }

                        canGestureSeek = false
                        isGestureSeeking = false
                        gestureSeekHint = null
                        controlsVisible = true
                    },
                    onDragCancel = {
                        canGestureSeek = false
                        isGestureSeeking = false
                        gestureSeekHint = null
                    }
                )
            }
            .pointerInput(isFullScreenChanging) {
                detectTapGestures(
                    /**
                     * 单击：显示 / 隐藏控制栏。
                     */
                    onTap = {
                        if (isFullScreenChanging) return@detectTapGestures
                        controlsVisible = !controlsVisible
                    },

                    /**
                     * 双击：
                     * 左 1/3 快退
                     * 中间 1/3 播放 / 暂停
                     * 右 1/3 快进
                     */
                    onDoubleTap = { offset: Offset ->
                        if (isFullScreenChanging) return@detectTapGestures

                        val width = size.width
                        val part = width / 3f

                        when {
                            offset.x < part -> {
                                val target = (exoPlayer.currentPosition - SEEK_STEP_MS)
                                    .coerceAtLeast(0L)

                                exoPlayer.seekTo(target)
                                isEnded = false
                                seekHint = SeekHint.Backward
                                controlsVisible = true
                            }

                            offset.x > part * 2 -> {
                                val safeDuration = exoPlayer.duration
                                    .takeIf { it > 0 }
                                    ?: Long.MAX_VALUE

                                val target = (exoPlayer.currentPosition + SEEK_STEP_MS)
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
                }
            },
            update = { playerView ->
                if (playerView.player !== exoPlayer) {
                    playerView.player = exoPlayer
                }
            },
            onRelease = { playerView ->
                playerView.player = null
            }
        )

        /**
         * 加载中遮罩。
         * 只显示转圈，不显示“加载中...”文字。
         */
        if (isBuffering) {
            PlayerLoadingOverlay()
        }

        /**
         * 双击快进 / 快退提示。
         */
        SeekHintOverlay(
            seekHint = seekHint
        )

        /**
         * 横向滑动调整进度时的中间提示。
         */
        GestureSeekOverlay(
            isVisible = isGestureSeeking,
            seekHint = gestureSeekHint,
            previewPosition = gestureSeekPosition,
            duration = duration
        )

        /**
         * 控制栏。
         */
        AnimatedVisibility(
            visible = controlsVisible && !isFullScreenChanging,
            modifier = Modifier.fillMaxSize()
        ) {
            PlayerControlsOverlay(
                title = title,
                isPlaying = isPlaying,
                currentPosition = if (isGestureSeeking) {
                    gestureSeekPosition
                } else {
                    currentPosition
                },
                duration = duration,
                isDraggingProgress = isDraggingProgress,
                previewPosition = previewPosition,
                speed = speed,
                episodes = episodes,
                currentEpisode = currentEpisode,
                qualities = qualities,
                currentQuality = currentQuality,
                isFullScreen = isFullScreen,
                onBackClick = onBackClick,
                onHomeClick = onHomeClick,
                onMoreClick = onMoreClick,
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
                    onFullScreenClick()
                    controlsVisible = true
                },
                onMenuExpandedChange = { expanded ->
                    isMenuExpanded = expanded
                    controlsVisible = true
                },
            )
        }
    }
}

/**
 * 控制栏组件。
 *
 * 普通模式：顶部只显示返回、主页、更多；底部只显示播放、进度、全屏。
 * 全屏模式：顶部显示标题；底部显示倍速、清晰度、选集。
 */
@Composable
private fun PlayerControlsOverlay(
    title: String?,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    isDraggingProgress: Boolean,
    previewPosition: Long,
    speed: Float,
    episodes: List<PlayEpisode>,
    currentEpisode: PlayEpisode?,
    qualities: List<String>,
    currentQuality: String,
    isFullScreen: Boolean,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onMoreClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onSliderValueChange: (Float) -> Unit,
    onSliderValueChangeFinished: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    onEpisodeSelected: (PlayEpisode) -> Unit,
    onQualitySelected: (String) -> Unit,
    onFullScreenClick: () -> Unit,
    onMenuExpandedChange: (Boolean) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        TopPlayerBar(
            title = title,
            isFullScreen = isFullScreen,
            onBackClick = onBackClick,
            onHomeClick = onHomeClick,
            onMoreClick = onMoreClick,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        /**
         * 拖动进度条时显示当前预览时间。
         */
        if (isDraggingProgress) {
            Text(
                text = formatPlayerTime(previewPosition),
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 66.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        BottomPlayerBar(
            isPlaying = isPlaying,
            currentPosition = if (isDraggingProgress) {
                previewPosition
            } else {
                currentPosition
            },
            duration = duration,
            speed = speed,
            episodes = episodes,
            currentEpisode = currentEpisode,
            qualities = qualities,
            currentQuality = currentQuality,
            isFullScreen = isFullScreen,
            onPlayPauseClick = onPlayPauseClick,
            onSliderValueChange = onSliderValueChange,
            onSliderValueChangeFinished = onSliderValueChangeFinished,
            onSpeedSelected = onSpeedSelected,
            onEpisodeSelected = onEpisodeSelected,
            onQualitySelected = onQualitySelected,
            onFullScreenClick = onFullScreenClick,
            onMenuExpandedChange = onMenuExpandedChange,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * 顶部控制栏。
 *
 * 普通模式不显示标题。
 * 全屏模式才显示标题。
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
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.68f),
                        Color.Transparent
                    )
                )
            )
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "返回",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        if (!isFullScreen){
            SpaceSmallWidth()
            IconButton(
                onClick = onHomeClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "主页",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (isFullScreen) {
            Text(
                text = title.orEmpty(),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        IconButton(
            onClick = onMoreClick,
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "更多",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * 底部控制栏。
 *
 * 普通模式精简显示。
 * 全屏模式再显示倍速、清晰度、选集。
 */
@Composable
private fun BottomPlayerBar(
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    speed: Float,
    episodes: List<PlayEpisode>,
    currentEpisode: PlayEpisode?,
    qualities: List<String>,
    currentQuality: String,
    isFullScreen: Boolean,
    onPlayPauseClick: () -> Unit,
    onSliderValueChange: (Float) -> Unit,
    onSliderValueChangeFinished: () -> Unit,
    onSpeedSelected: (Float) -> Unit,
    onEpisodeSelected: (PlayEpisode) -> Unit,
    onQualitySelected: (String) -> Unit,
    onFullScreenClick: () -> Unit,
    onMenuExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.78f)
                    )
                )
            )
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPlayPauseClick
        ) {
            Icon(
                imageVector = if (isPlaying) {
                    Icons.Default.Pause
                } else {
                    Icons.Default.PlayArrow
                },
                contentDescription = "播放暂停",
                tint = Color.White
            )
        }

        Text(
            text = formatPlayerTime(currentPosition),
            color = Color.White
        )

        PlayerProgressBar(
            currentPosition = currentPosition,
            duration = duration,
            onValueChange = onSliderValueChange,
            onValueChangeFinished = onSliderValueChangeFinished,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        )

        Text(
            text = formatPlayerTime(duration),
            color = Color.White
        )

        if (isFullScreen) {
            PlayerSpeedButton(
                speed = speed,
                onSpeedSelected = onSpeedSelected,
                onMenuExpandedChange = onMenuExpandedChange
            )

            PlayerQualityButton(
                qualities = qualities,
                currentQuality = currentQuality,
                onQualitySelected = onQualitySelected,
                onMenuExpandedChange = onMenuExpandedChange
            )

            PlayerEpisodeButton(
                episodes = episodes,
                currentEpisode = currentEpisode,
                onEpisodeSelected = onEpisodeSelected,
                onMenuExpandedChange = onMenuExpandedChange
            )
        }

        IconButton(
            onClick = onFullScreenClick
        ) {
            Icon(
                imageVector = if (isFullScreen) {
                    Icons.Default.FullscreenExit
                } else {
                    Icons.Default.Fullscreen
                },
                contentDescription = "全屏",
                tint = Color.White
            )
        }
    }
}

/**
 * 自定义进度条。
 *
 * 不使用 Material3 Slider 的默认 thumb，避免出现竖线样式。
 * 这里使用一个白色实心小圆球作为拖动点。
 */
@Composable
private fun PlayerProgressBar(
    currentPosition: Long,
    duration: Long,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var barWidthPx by remember {
        mutableStateOf(1)
    }

    val safeDuration = duration.coerceAtLeast(1L)
    val progress = (currentPosition.toFloat() / safeDuration.toFloat())
        .coerceIn(0f, 1f)

    fun updateProgressByOffset(x: Float) {
        val newProgress = (x / barWidthPx.toFloat())
            .coerceIn(0f, 1f)
        val newPosition = newProgress * safeDuration.toFloat()
        onValueChange(newPosition)
    }

    BoxWithConstraints(
        modifier = modifier
            .height(28.dp)
            .onSizeChanged { size ->
                barWidthPx = size.width.coerceAtLeast(1)
            }
            .pointerInput(safeDuration) {
                detectTapGestures { offset ->
                    updateProgressByOffset(offset.x)
                    onValueChangeFinished()
                }
            }
            .pointerInput(safeDuration) {
                detectDragGestures(
                    onDragStart = { offset ->
                        updateProgressByOffset(offset.x)
                    },
                    onDragEnd = {
                        onValueChangeFinished()
                    },
                    onDragCancel = {
                        onValueChangeFinished()
                    },
                    onDrag = { change, _ ->
                        updateProgressByOffset(change.position.x)
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
                    color = Color.White.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(99.dp)
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .height(3.dp)
                .align(Alignment.CenterStart)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(99.dp)
                )
        )

        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(thumbSize)
                .align(Alignment.CenterStart)
                .background(
                    color = Color.White,
                    shape = CircleShape
                )
        )
    }
}

/**
 * 倍速按钮。
 * 只在全屏模式显示。
 */
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
                text = "${speed}x",
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

/**
 * 清晰度按钮。
 * 只在全屏模式显示。
 */
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

/**
 * 选集按钮。
 * 只在全屏模式显示。
 */
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
 * 加载中遮罩。
 *
 * 只显示转圈，不显示“加载中...”文字。
 */
@Composable
private fun PlayerLoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.18f)),
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
 * 横向滑动调整进度提示。
 */
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
                    color = Color.Black.copy(alpha = 0.58f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = 18.dp, vertical = 10.dp)
        )
    }
}

/**
 * 快进快退提示。
 */
@Composable
private fun SeekHintOverlay(
    seekHint: SeekHint?
) {
    if (seekHint == null) return

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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

        Row(
            modifier = Modifier
                .align(alignment)
                .padding(horizontal = 42.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(32.dp)
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

/**
 * 时间格式化。
 */
private fun formatPlayerTime(timeMs: Long): String {
    if (timeMs <= 0L) {
        return "00:00"
    }

    val totalSeconds = timeMs / 1000
    val hours = totalSeconds / 3600
    val minutes = totalSeconds % 3600 / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        "%02d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%02d:%02d".format(minutes, seconds)
    }
}
