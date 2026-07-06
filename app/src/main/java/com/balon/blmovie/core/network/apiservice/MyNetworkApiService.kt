package com.balon.blmovie.core.network.apiservice

import com.balon.blmovie.core.model.Article
import com.balon.blmovie.core.model.Category
import com.balon.blmovie.core.model.HomeData
import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.core.model.response.NetworkPageData
import com.balon.blmovie.core.model.response.NetworkResponse
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 * 1
 * 网络请求接口
 */
interface MyNetworkApiService {

    /**
     * 首页轮播图
     * {
     *   "status": 0,
     *   "message": "success",
     *   "data": [
     *     {
     *       "area": "日本",
     *       "lastTime": "2026-07-02T23:58:01",
     *       "name": "库兹马唱歌的话家里哆啰啰",
     *       "note": "已完结",
     *       "pic": "https://img.lzipic.com/upload/vod/20260409-1/b5e37f2d9ffdeebb24aace021dc1575d.webp",
     *       "typeName": "日韩动漫",
     *       "vodId": 133458,
     *       "year": "2026"
     *     },
     *     ...
     *    ]
     *  }
     */
    @GET("api/v1/videos/banners")
    suspend fun banners(): NetworkResponse<List<Movie>>

    /**
     * 影视类别
     */
    @GET("api/v1/categories")
    suspend fun categories(): NetworkResponse<List<Category>>

    // 动作片6 喜剧片7 爱情片8 科幻片9 恐怖片10 剧情片11 国产剧13 欧美剧16
    // 记录片20 大陆综艺25 日韩动漫30 国产动漫29 欧美动漫31 AI漫剧52

    @GET("api/v1/videos")
    suspend fun japanKoreanMovie(
        @QueryMap data: Map<String, String>
    ): NetworkResponse<NetworkPageData<Movie>>


    @GET("api/v1/home")
    suspend fun homeData(
    ): NetworkResponse<HomeData>


    @GET("api/v1/articles")
    suspend fun articleList(
        @QueryMap data:Map<String, String>
    ): NetworkResponse<NetworkPageData<Article>>

}