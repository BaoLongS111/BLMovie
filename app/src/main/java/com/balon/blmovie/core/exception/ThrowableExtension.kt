package com.balon.blmovie.core.exception

import com.balon.blmovie.MyApplication
import com.balon.blmovie.R
import com.balon.blmovie.util.NetworkUtil

fun Throwable.tipData(): TipData {
    val tipData = TipData()
    if (this is CommonException) {
        // 网络API错误
        tipData.tipMessage = this.networkResponse?.message
            ?: MyApplication.instance.getString(R.string.unknown_error)
        return tipData

    } else {
        if (NetworkUtil.isNetworkAvailable(MyApplication.instance)) {
            // 有网
            tipData.tipMessage =
                MyApplication.instance.getString(R.string.error_loading, this.localizedMessage)
            return tipData
        } else {
            // 没网
            tipData.tipMessage =
                MyApplication.instance.getString(R.string.error_network_not_connect)
            return tipData
        }
    }
}
data class TipData(
    var tipMessage: String = MyApplication.instance.getString(R.string.unknown_error),
    var tipIcon: Int = R.drawable.bg_error
)