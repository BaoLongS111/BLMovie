package com.balon.blmovie

import android.app.Application
import com.balon.blmovie.core.config.Config
import com.hjq.toast.Toaster
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        initLog()
        initToast()
    }

    private fun initToast(){
        // 初始化 Toast 框架
        Toaster.init(this)
    }

    /**
     * 初始化日志
     */
    private fun initLog() {
        /**
         * debug 模式下种一棵 DebugTree
         * 以后 Timber.d / Timber.e 才会真正打印日志
         * release 模式下不种树，日志默认不输出
         */
        if (Config.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            //可以上报到任何地方，真实项目大部分都会用第三方日志服务
            //例如：阿里云日志服务，或者自己公司有；打印到文件那种现在用的太少了，所以也就不实现了
        }
    }

    companion object {
        lateinit var instance: MyApplication
    }
}