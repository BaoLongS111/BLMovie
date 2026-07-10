plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)

    // kotlin序列化
    alias(libs.plugins.kotlin.serialization)

    // ksp和hilt
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.balon.blmovie"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.balon.blmovie"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    //region 偏好设置和渠道
    flavorDimensions += listOf("env", "channel")
    productFlavors {
        create("dev") {
            dimension = "env"
            //API端点
            buildConfigField("String", "ENDPOINT", "\"http://119.23.146.194:8080/\"")
        }
        create("local") {
            dimension = "env"
            //API端点
            buildConfigField("String", "ENDPOINT", "\"http://127.0.0.1:3307/\"")
        }
        create("default") {
            dimension = "channel"
            versionNameSuffix = ".default"
        }
        create("play") {
            dimension = "channel"
            versionNameSuffix = "-play"
        }
    }
    //end region
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // material-icons扩展
    implementation(libs.androidx.compose.material.icons.extended)

    // navigation3导航
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // ksp和hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // 系统splash screen
    implementation(libs.androidx.core.splashscreen)

    // 内存泄露检测，只在 debug 包生效
    debugImplementation(libs.leakcanary.android)

    // 网络请求检测，debug 用真库，release 用空实现
    debugImplementation(libs.chucker.library)
    releaseImplementation(libs.chucker.library.no.op)

    // region 网络请求
    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    // Kotlinx Serialization JSON
    implementation(libs.kotlinx.serialization.json)
    // end region

    // 日志打印
    implementation(libs.timber)

    // 指示器
    implementation("com.tbuonomo:dotsindicator:5.1.1")

    // Coil 图片加载
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // lottie动画
    implementation(libs.lottie.compose)

    // 吐司框架：https://github.com/getActivity/Toaster
    implementation("com.github.getActivity:Toaster:15.0")

    // 视频播放器
    implementation("androidx.media3:media3-exoplayer:1.10.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.10.1")
    implementation("androidx.media3:media3-ui:1.10.1")
    implementation("androidx.media3:media3-ui-compose:1.10.1")

    // 提取视频指定位置的预览画面
    implementation("androidx.media3:media3-inspector:1.10.1")
    implementation("androidx.media3:media3-inspector-frame:1.10.1")

    // 让 FrameExtractor 返回的 ListenableFuture 可以使用 await()
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.11.0")

}