package com.balon.blmovie.core.config

import com.balon.blmovie.BuildConfig
/**
 * 配置文件
 * buildFeatures {
 *         compose = true
 *         buildConfig = true
 *  }
 *  .\gradlew.bat :app:generateDevDefaultDebugBuildConfig
 */
object Config {

    /**
     * 是否是调试模式
     */
    val DEBUG: Boolean = BuildConfig.DEBUG

    /**
     * 端点
     */
    const val ENDPOINT = BuildConfig.ENDPOINT
}