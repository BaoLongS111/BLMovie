package com.balon.blmovie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.balon.blmovie.core.design.theme.BLMovieTheme
import com.balon.blmovie.ui.MyApp
import dagger.hilt.android.AndroidEntryPoint

/**
 * 主界面
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 系统启动Splash
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BLMovieTheme {
                MyApp()
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BLMovieTheme {
        MyApp()
    }
}
