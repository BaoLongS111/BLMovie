package com.balon.blmovie.core.network.datasource

import android.net.Network
import com.balon.blmovie.core.model.Article
import com.balon.blmovie.core.model.Category
import com.balon.blmovie.core.model.HomeData
import com.balon.blmovie.core.model.MovieAdult
import com.balon.blmovie.core.model.MovieAdultDetail
import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.core.model.response.NetworkPageData
import com.balon.blmovie.core.model.response.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * 2
 * 网络数据源
 * 封装这个的目的是将Api接口和具体的实现隔离开来，这只是定义接口，不管是谁实现的，谁提供的
 * 就比如我会写一个RetrofitDatasource来实现，那以后也可以写一个xxDatasource来实现
 * 这样写就不会强依赖一个网络请求框架
 */
interface MyNetworkDatasource {
    /**
     * 首页轮播图
     */
    suspend fun banners(): NetworkResponse<List<Movie>>

    /**
     * 影视类别
     */
    suspend fun categories(): NetworkResponse<List<Category>>

    suspend fun japanKoreanMovie(
        data:Map<String,String>
    ): NetworkResponse<NetworkPageData<Movie>>

    suspend fun homeData(): NetworkResponse<HomeData>

    suspend fun articleList(
        data:Map<String, String>
    ): NetworkResponse<NetworkPageData<Article>>

    /**
     * 影视详情
     */
    suspend fun movieDetail(
        movieId: Long
    ): NetworkResponse<Movie>

    suspend fun searchMovie(
        data:Map<String, String>
    ): NetworkResponse<NetworkPageData<Movie>>

    /**
     * 获取影视Adult详情
     */
    suspend fun movieAdultDetail(
        movieAdultId: Long
    ): NetworkResponse<MovieAdultDetail>

    suspend fun searchMovieAdult(
       data:Map<String, String>
    ): NetworkResponse<NetworkPageData<MovieAdult>>

    suspend fun searchMovieSuggest(
        @Query("keyword") keyword:String,
        @Query("size") size: String = "20"
    ): NetworkResponse<List<String>>

}