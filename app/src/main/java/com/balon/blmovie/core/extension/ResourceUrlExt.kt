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