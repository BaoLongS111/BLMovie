package com.balon.blmovie.core.network.di

import com.balon.blmovie.core.network.datasource.MyNetworkDatasource
import com.balon.blmovie.core.network.datasource.MyRetrofitDatasource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 5
 * 渠道网络模块
 *
 * 例如：dev环境有很多本地测试数据
 * prod连接线上API
 *
 * 但我们这里没有这种需求，所以就直接放到main目录
 * 参考：https://github.com/search?q=repo%3Aandroid%2Fnowinandroid%20FlavoredNetworkModule&type=code
 */
@Module
@InstallIn(SingletonComponent::class)
interface FlavoredNetworkModule {
    /**
     * 把MyRetrofitDatasource暴露出去
     * 以后依赖注入谁需要MyNetworkDatasource，就会把MyRetrofitDatasource送过去
     */
    @Binds
    fun binds(impl: MyRetrofitDatasource): MyNetworkDatasource
}