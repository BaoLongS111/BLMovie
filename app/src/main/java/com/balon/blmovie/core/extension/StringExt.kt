package com.balon.blmovie.core.extension

import com.balon.blmovie.core.model.PlayEpisode

/**
 * 给pic的url加上http://开头，否则coil加载不了
 */
fun String?.toImageUrl(): String {
    if (this.isNullOrBlank()) {
        return ""
    }

    return when {
        this.startsWith("//") -> {
            "https:$this"
        }

        this.startsWith("http://") || this.startsWith("https://") -> {
            this
        }

        else -> {
            this
        }
    }
}




/**
 * 解析播放地址
 * 第01集$https://xxx/playlist.m3u8#第02集$https://xxx/playlist.m3u8
 */
fun String?.parsePlayEpisodes(): List<PlayEpisode> {
    if (this.isNullOrBlank()) return emptyList()

    return this
        .split("#")
        .mapNotNull { item ->
            val parts = item.split("$")

            if (parts.size < 2) {
                return@mapNotNull null
            }

            val name = parts[0].trim()
            val url = parts[1].trim().toFullUrl()

            if (url.isBlank()) {
                null
            } else {
                PlayEpisode(
                    name = name.ifBlank { "播放" },
                    url = url
                )
            }
        }
}

fun String.toFullUrl(): String {
    return when {
        startsWith("//") -> "https:$this"
        startsWith("http://") || startsWith("https://") -> this
        else -> this
    }
}