package com.balon.blmovie.core.exception

import com.balon.blmovie.core.model.response.NetworkResponse

/**
 * 通用异常
 */
class CommonException(
    val networkResponse: NetworkResponse<*>? = null
): RuntimeException()