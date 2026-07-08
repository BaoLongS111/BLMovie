package com.balon.blmovie.core.network.di

import com.balon.blmovie.MyApplication
import com.balon.blmovie.core.config.Config
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton
import java.util.concurrent.TimeUnit

/**
 * 3
 * 网络依赖注入模块
 */
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    /**
     * 提供网络Json解析器
     */
    @Provides
    @Singleton
    fun providesNetworkJson(): Json{
        return Json{
            ignoreUnknownKeys = true
        }
    }

    /**
     * 如果想提供已有子类的父类，就可以这样转换一下
     * 因为Call.Factory是OkHttpClient的父类，这叫向上转型，属于多态的一种
     */
    @Provides
    @Singleton
    fun providesOkHttpCallFactory(okHttpClient: OkHttpClient): Call.Factory{
        return okHttpClient
    }


    /**
     * 提供 OkHttpClient
     */
    @Provides
    @Singleton
    fun providesOkHttpClient() : OkHttpClient{
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)  //连接超时时间
            .writeTimeout(10, TimeUnit.SECONDS)  // 写超时时间
            .readTimeout(10, TimeUnit.SECONDS)   // 读超时时间
            //添加拦截器
            .addInterceptor(HttpLoggingInterceptor().apply {
                // 如果是DEBUG模式下，每次网络请求都会在LogCat打印出请求体
                setLevel(
                    if (Config.DEBUG)
                        HttpLoggingInterceptor.Level.BODY
                    else
                        HttpLoggingInterceptor.Level.NONE
                )
            })
            .apply {
                //添加chucker实现应用内显示网络请求信息拦截器
                if (Config.DEBUG) {
                    addInterceptor(ChuckerInterceptor.Builder(MyApplication.instance).build())
                }
            }
            .addInterceptor(JsonFixInterceptor())
            .build()
    }

}

class JsonFixInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val body = response.body ?: return response
        val contentType = body.contentType()
        val rawJson = body.string()

        val fixedJson = rawJson.replace(
            Regex("\"vodDoubanScore\"\\s*:\\s*(\\d+)\\.\\.(\\d+)"),
            "\"vodDoubanScore\":$1.$2"
        )

        return response.newBuilder()
            .body(fixedJson.toResponseBody(contentType))
            .build()
    }
}