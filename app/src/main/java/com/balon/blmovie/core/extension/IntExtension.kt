package com.balon.blmovie.core.extension

import android.widget.Toast
import com.balon.blmovie.MyApplication


/**
 * Int 扩展toast
 */

/**
 * 短toast
 */
fun Int.shortToast() {
    Toast.makeText(MyApplication.instance, this, Toast.LENGTH_SHORT).show()
}

/**
 * 长toast
 */
fun Int.longToast() {
    Toast.makeText(MyApplication.instance, this, Toast.LENGTH_LONG).show()
}