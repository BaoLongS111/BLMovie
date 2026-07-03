package com.balon.blmovie.core.extension


import com.balon.blmovie.core.exception.CommonException
import com.balon.blmovie.core.model.response.NetworkResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

/**
 * 转为Result
 */
fun <T> Flow<T>.asResult(): Flow<Result<T>> = map {
    if (it is NetworkResponse<*>) {
        if (it.isSucceed) {
            Result.success(it)
        } else {
            if (it.status == 401) {
                //未登录，或者登录信息失效
//                MyApplication.instance.logout()
            }
            Result.failure(CommonException(it))
        }
    } else {
        Result.success(it)
    }
}
    .onStart {
//    emit(Result.loading())
    }.catch {
        emit(Result.failure(it))
    }