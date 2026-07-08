package com.balon.blmovie.core.network.datasource

import com.balon.blmovie.core.config.Config
import com.balon.blmovie.core.model.Article
import com.balon.blmovie.core.model.Category
import com.balon.blmovie.core.model.HomeData
import com.balon.blmovie.core.model.MovieAdult
import com.balon.blmovie.core.model.MovieAdultDetail
import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.core.model.response.NetworkPageData
import com.balon.blmovie.core.model.response.NetworkResponse
import com.balon.blmovie.core.network.apiservice.MyNetworkApiService
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 4
 * Retrofit数据源
 */
@Singleton
class MyRetrofitDatasource @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: Call.Factory
): MyNetworkDatasource {

    /**
     * 网络请求接口Service
     */
    private val service = Retrofit.Builder()
        .baseUrl(Config.ENDPOINT)
        .callFactory(okhttpCallFactory)  // 添加okhttp拦截，自定义一些功能
        .addConverterFactory(
            // 这一步是将请求下来的Json可以转成对象，以及反过来把对象转成Json Post给服务器
            networkJson.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(MyNetworkApiService::class.java)  // 和Api绑定

    /**
     * 轮播图
     */
    override suspend fun banners(): NetworkResponse<List<Movie>> {
        return service.banners()
    }

    /**
     * 影视类别
     */
    override suspend fun categories(): NetworkResponse<List<Category>> {
        return service.categories()
    }

    override suspend fun japanKoreanMovie(data: Map<String, String>): NetworkResponse<NetworkPageData<Movie>> {
        return service.japanKoreanMovie(data)
    }

    override suspend fun homeData(): NetworkResponse<HomeData> {
        return service.homeData()
    }

    override suspend fun articleList(
        data:Map<String,String>
    ): NetworkResponse<NetworkPageData<Article>> {
        return service.articleList(data)
    }

    override suspend fun movieDetail(movieId: Long): NetworkResponse<Movie> {
        return service.movieDetail(movieId = movieId)
    }

    override suspend fun searchMovie(data: Map<String, String>): NetworkResponse<NetworkPageData<Movie>> {
        return service.searchMovie(data = data)
    }

    override suspend fun movieAdultDetail(movieAdultId: Long): NetworkResponse<MovieAdultDetail> {
        return service.movieAdultDetail(movieAdultId)
    }

    override suspend fun searchMovieAdult(data: Map<String, String>): NetworkResponse<NetworkPageData<MovieAdult>> {
        return service.searchMovieAdult(data)
    }


}