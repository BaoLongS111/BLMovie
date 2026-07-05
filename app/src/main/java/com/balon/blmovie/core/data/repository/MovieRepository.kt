package com.balon.blmovie.core.data.repository

import com.balon.blmovie.core.model.Category
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
 * 视频仓库
 */
class MovieRepository @Inject constructor(
    private val networkDatasource: MyNetworkDatasource
) {
    // suspend：适合一次性异步任务
    // Flow：适合一串异步数据流
    /** suspend 请求一次，返回一次
     * 调用一次
     *     ↓
     * 请求一次
     *     ↓
     * 返回一个结果
     * 适合：商品详情 电影详情 登录 注册 收藏 提交订单 删除记录
     */

    /** Flow：可以持续发射多个结果
     *
     * Loading
     *     ↓
     * 缓存数据
     *     ↓
     * 网络数据
     *     ↓
     * 错误信息
     *
     * 或者：
     * 数据库第一次数据
     *     ↓
     * 数据库变化后的数据
     *     ↓
     * 再次变化后的数据
     *
     * 数据库监听 本地缓存 + 网络刷新 分页数据流 搜索关键词变化 用户登录状态变化 购物车列表变化
     */

    fun banners(): Flow<NetworkResponse<List<Movie>>> {
        return flow {
            emit(
                networkDatasource.banners()
            )
        }.flowOn(Dispatchers.IO)
    }

    fun categories(): Flow<NetworkResponse<List<Category>>> = flow {
        emit(networkDatasource.categories())
    }.flowOn(Dispatchers.IO)

    fun japanKoreanMovie(data: Map<String, String>): Flow<NetworkResponse<NetworkPageData<Movie>>> =
        flow {
            emit(networkDatasource.japanKoreanMovie(data))
        }.flowOn(Dispatchers.IO)


}