package com.balon.blmovie.core.data.repository

import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.core.model.response.NetworkResponse
import com.balon.blmovie.core.network.datasource.MyNetworkDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * 视频详情仓库
 */
class MovieDetailRepository @Inject constructor(
    private val networkDatasource: MyNetworkDatasource
) {

    /**
     * 视频详情
     */
    fun movieDetail(
        movieId: Long
    ):Flow<NetworkResponse<Movie>> = flow{
        emit(networkDatasource.movieDetail(movieId = movieId))
    }.flowOn(Dispatchers.IO)

}