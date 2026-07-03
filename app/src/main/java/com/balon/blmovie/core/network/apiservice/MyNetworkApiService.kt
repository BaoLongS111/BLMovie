package com.balon.blmovie.core.network.apiservice

import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.core.model.response.NetworkPageData
import com.balon.blmovie.core.model.response.NetworkResponse
import retrofit2.http.GET

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

}