package com.balon.blmovie.core.model.response

import kotlinx.serialization.Serializable

/**
 * 分页模型
 */
@Serializable
data class NetworkPageMeta(
    /**
     * 有多少条
     */
    val total: Int? = null,

    /**
     * 有多少页
     */
    val pages: Int? = null,

    /**
     * 当前每页显示多少条
     */
    val size: Int? = null,

    /**
     * 当前页
     */
    val page: Int? = null,

    /**
     * 下一页
     */
    val next: Int? = null,
)