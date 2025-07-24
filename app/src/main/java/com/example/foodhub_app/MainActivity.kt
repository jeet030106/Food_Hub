package com.example.foodhub_app

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.ui.feature.auth.AuthScreen
import com.example.foodhub_app.ui.feature.auth.signup.SignUpScreen
import com.example.foodhub_app.ui.theme.FoodHub_AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var splashScreen=true
    @Inject
    lateinit var foodApi: FoodApi
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition { splashScreen }
            setOnExitAnimationListener { splashScreenView ->
                val zoomX = ObjectAnimator.ofFloat(splashScreenView.iconView, View.SCALE_X, 1f, 0f)
                val zoomY = ObjectAnimator.ofFloat(splashScreenView.iconView, View.SCALE_Y, 1f, 0f)
                zoomX.duration = 500
                zoomY.duration = 500
                zoomX.interpolator = OvershootInterpolator()
                zoomY.interpolator = OvershootInterpolator()
                zoomX.doOnEnd { splashScreenView.remove() }
                zoomY.doOnEnd { splashScreenView.remove() }
                zoomX.start()
                zoomY.start()
            }
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SignUpScreen()
        }
        if(::foodApi.isInitialized){
            Log.d("Jeet","Initialized")
        }
        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            splashScreen=false
        }
    }
}
