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
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
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
import androidx.navigation.toRoute
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.FoodHubSession
import com.example.foodhub_app.data.model.FoodItem
import com.example.foodhub_app.data.model.SignInRequest
import com.example.foodhub_app.ui.feature.auth.AuthScreen
import com.example.foodhub_app.ui.feature.auth.login.SignInScreen
import com.example.foodhub_app.ui.feature.auth.signup.SignUpScreen
import com.example.foodhub_app.ui.feature.cart.CartScreen
import com.example.foodhub_app.ui.feature.food_item_details.FoodDetailsScreen
import com.example.foodhub_app.ui.feature.home.HomeScreen
import com.example.foodhub_app.ui.feature.restaurant_details.RestaurantDetailScreen
import com.example.foodhub_app.ui.navigation.Auth
import com.example.foodhub_app.ui.navigation.Cart
import com.example.foodhub_app.ui.navigation.FoodDetails
import com.example.foodhub_app.ui.navigation.Home
import com.example.foodhub_app.ui.navigation.Login
import com.example.foodhub_app.ui.navigation.RestaurantDetail
import com.example.foodhub_app.ui.navigation.SignUp
import com.example.foodhub_app.ui.navigation.foodItemNavType
import com.example.foodhub_app.ui.theme.CustomNavHost
import com.example.foodhub_app.ui.theme.FoodHub_AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import javax.inject.Inject
import kotlin.reflect.typeOf

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    var splashScreen=true
    @Inject
    lateinit var foodApi: FoodApi
    @Inject
    lateinit var session: FoodHubSession
    @OptIn(ExperimentalSharedTransitionApi::class)
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
            SharedTransitionLayout {
                CustomNavHost(
                    navController = navController,
                    startDestination = if (session.getToken() == null) Auth else Home
                ) {
                    composable<SignUp> {
                        SignUpScreen(navController)
                    }
                    composable<Auth> {
                        AuthScreen(navController)
                    }
                    composable<Login> {
                        SignInScreen(navController)
                    }
                    composable<Home> {
                        // Pass 'this' which is the AnimatedVisibilityScope
                        HomeScreen(
                            navController,
                            this
                        )
                    }
                    composable<RestaurantDetail> {
                        val route = it.toRoute<RestaurantDetail>()
                        // Pass 'this' which is the AnimatedVisibilityScope
                        RestaurantDetailScreen(
                            navController, route.name, route.imageUrl, route.restaurantId,
                            this
                        )
                    }
                    composable<FoodDetails>(
                        typeMap=mapOf(
                            typeOf<FoodItem>() to foodItemNavType
                        )
                    ){
                        val route = it.toRoute<FoodDetails>()
                        FoodDetailsScreen(navController, this, route.foodItem)
                    }
                    composable<Cart> {
                        CartScreen(navController)
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
