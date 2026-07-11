package com.balon.blmovie.core.data.repository

import com.balon.blmovie.core.model.MovieAdult
import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.core.model.response.NetworkPageData
import com.balon.blmovie.core.model.response.NetworkResponse
import com.balon.blmovie.core.network.datasource.MyNetworkDatasource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * 搜索页面接口仓库
 */
class SearchRepository @Inject constructor(
    private val networkDatasource: MyNetworkDatasource
) {

    fun searchMovie(
        data: Map<String, String>
    ): Flow<NetworkResponse<NetworkPageData<Movie>>> =
        flow {
            emit(
                networkDatasource.searchMovie(data)
            )
        }.flowOn(Dispatchers.IO)

    fun searchMovieAdult(
        data: Map<String, String>
    ): Flow<NetworkResponse<NetworkPageData<MovieAdult>>> =
        flow {
            emit(
                networkDatasource.searchMovieAdult(data)
            )
        }.flowOn(Dispatchers.IO)

    fun searchMovieSuggest(
        keyword:String
    ): Flow<NetworkResponse<List<String>>> = flow<NetworkResponse<List<String>>> {
        emit(
            networkDatasource.searchMovieSuggest(keyword)
        )
    }.flowOn(Dispatchers.IO)

}