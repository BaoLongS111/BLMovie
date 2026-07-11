package com.balon.blmovie.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.hjq.toast.Toaster

fun openBrowser(
    context: Context,
    url: String
) {
    val realUrl = if (
        url.startsWith("http://") ||
        url.startsWith("https://")
    ) {
        url
    } else {
        "https://$url"
    }

    try {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(realUrl)
        )

        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toaster.show("没有找到可打开网页的应用")
    }
}