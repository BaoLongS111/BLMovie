package com.balon.blmovie.core.extension

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
data class PlayEpisode(
    val name: String,
    val url: String
)
fun String.parsePlayEpisodes(): List<PlayEpisode> {
    if (this.isBlank()) return emptyList()

    return this.split("#")
        .mapNotNull { item ->
            val parts = item.split("$", limit = 2)

            if (parts.size == 2) {
                PlayEpisode(
                    name = parts[0].trim(),
                    url = parts[1].trim()
                )
            } else {
                null
            }
        }
}