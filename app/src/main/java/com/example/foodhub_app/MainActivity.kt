package com.example.foodhub_app

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Popup
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.model.SignInRequest
import com.example.foodhub_app.ui.feature.auth.AuthScreen
import com.example.foodhub_app.ui.feature.auth.login.SignInScreen
import com.example.foodhub_app.ui.feature.auth.signup.SignUpScreen
import com.example.foodhub_app.ui.navigation.Auth
import com.example.foodhub_app.ui.navigation.Home
import com.example.foodhub_app.ui.navigation.Login
import com.example.foodhub_app.ui.navigation.SignUp
import com.example.foodhub_app.ui.theme.CustomNavHost
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
            val navController=rememberNavController()
            val navHost= CustomNavHost(navController = navController, startDestination = Auth,
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700),
                    )+ fadeIn(animationSpec = tween(700), initialAlpha = 0.2f)
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(700),
                    )+ fadeOut(animationSpec = tween(700), targetAlpha = 1f)
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700),
                    )+ fadeIn(animationSpec = tween(700), initialAlpha = 0.2f)
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(700),
                    )+ fadeOut(animationSpec = tween(700), targetAlpha = 1f)
                },
            ){
                composable<SignUp>{
                    SignUpScreen(navController)
                }
                composable<Auth>{
                    AuthScreen(navController)
                }
                composable<Login>{
                    SignInScreen(navController)
                }
                composable<Home>{
                    Box(modifier= Modifier.fillMaxSize().background(Color.Red)){

                    }
                }
            }
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
