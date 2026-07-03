package com.balon.blmovie.core.model.response

import kotlinx.serialization.Serializable

/**
 * 解析网络响应
 */
@Serializable
data class NetworkResponse<T>(

    /**
     * 真实数据，类型是泛型
     */
    val data: T? = null,

    /**
     * 状态码
     * 0表示成功
     */
    val status: Int = 0,

    /**
     * 出错的提示信息
     * 出错了不一定有
     */
    val message: String? = null

){
    /**
     * 是否成功
     */
    val isSucceed : Boolean
        get() = status == 0

}