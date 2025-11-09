package com.example.foodhub_app

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.FoodHubSession

import com.example.foodhub_app.ui.feature.auth.AuthScreen
import com.example.foodhub_app.ui.feature.auth.login.SignInScreen
import com.example.foodhub_app.ui.feature.auth.signup.SignUpScreen

import com.example.foodhub_app.ui.navigation.Auth

import com.example.foodhub_app.ui.navigation.Home
import com.example.foodhub_app.ui.navigation.Login
import com.example.foodhub_app.ui.navigation.Notification

import com.example.foodhub_app.ui.navigation.SignUp

import com.example.foodhub_app.ui.navigation.navRoutes
import com.example.foodhub_app.ui.theme.CustomNavHost
import com.example.foodhub_app.ui.theme.FoodHub_AppTheme
import com.example.foodhub_app.ui.theme.Primary
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.reflect.typeOf
import com.example.foodhub_app.R
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    var splashScreen=true
    @Inject
    lateinit var foodApi: FoodApi
    @Inject
    lateinit var session: FoodHubSession

    sealed class BottomNavItems(val routes: navRoutes,val icon:Int){
        object Home:BottomNavItems(com.example.foodhub_app.ui.navigation.Home,R.drawable.ic_home)
        object Notification:BottomNavItems(com.example.foodhub_app.ui.navigation.Notification,R.drawable.ic_notification)
        object OrderList:BottomNavItems(com.example.foodhub_app.ui.navigation.OrderList,R.drawable.ic_order)
    }


    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenStarted {
            try {
                val token = FirebaseMessaging.getInstance().token.await() // using kotlinx-coroutines-play-services or similar helper
                Log.d("FCM_TEST", "Token: $token")
            } catch (e: Exception) {
                Log.w("FCM_TEST", "Token fetch failed", e)
            }
        }
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
            FoodHub_AppTheme {
                val showBottomBar=remember{
                    mutableStateOf(false)
                }
                val  navItems=listOf(BottomNavItems.Home,BottomNavItems.Notification,BottomNavItems.OrderList)
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize(),

                    bottomBar ={
                        val currentRoute=navController.currentBackStackEntryAsState().value?.destination
                        AnimatedVisibility(visible = showBottomBar.value) {
                            NavigationBar {
                                navItems.forEach { item ->
                                    val selected=currentRoute?.hierarchy?.any { it.route==item.routes::class.qualifiedName }==true
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = { navController.navigate(item.routes) },
                                        icon={
                                            Box(modifier = Modifier.size(48.dp)){
                                                Icon(painter = painterResource(id = item.icon), contentDescription = null,
                                                    tint = if (selected) Primary else Color.Gray,
                                                    modifier = Modifier.align(Alignment.Center))

                                            }
                                        }
                                    )
                                }
                            }
                        }

                    }
                ) { innerPadding ->



                    SharedTransitionLayout {
                        CustomNavHost(
                            // The padding is now applied directly here
                            modifier = Modifier.padding(innerPadding),
                            navController = navController,
                            startDestination = if (session.getToken() == null) Auth else Home
                        ) {
                            composable<SignUp> {
                                showBottomBar.value=false
                                SignUpScreen(navController)
                            }
                            composable<Auth> {
                                showBottomBar.value=false
                                AuthScreen(navController,false)
                            }
                            composable<Login> {
                                showBottomBar.value=false
                                SignInScreen(navController)
                            }
                            composable<Home> {
                                showBottomBar.value=true
                                RestaurantHomeScreen(
                                    navController,
                                    this as Context
                                )
                            }

                            composable<Notification>{
                                showBottomBar.value=true
                            }

                        }
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
@Composable
fun RestaurantHomeScreen(navController: NavController, context: Context) {
    Box(){
        Text("Home")
    }
}
