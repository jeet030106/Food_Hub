package com.example.foodhub_app.ui

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.auth.GoogleAuthUiProvider
import com.example.foodhub_app.data.model.AuthResponse
import com.example.foodhub_app.data.model.OAuthRequest
import com.example.foodhub_app.data.remote.ApiResponse
import com.example.foodhub_app.data.remote.safeApiCall
import com.example.foodhub_app.ui.feature.auth.login.SignInViewModel.SignInEvent
import com.example.foodhub_app.ui.feature.auth.login.SignInViewModel.SignInNavigation
import com.stripe.android.model.Token
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseAuthViewModel(open val foodApi: FoodApi): ViewModel() {
    var errorTitle:String=""
    var errorMsg:String=""
    val googleAuthUiProvider= GoogleAuthUiProvider()
    abstract fun loading()
    abstract fun success(token:String)
    abstract fun error(message:String)

    fun onClickedGoogleLogin(context: ComponentActivity){
        initiateGoogleLogin( context)
    }
    fun onClickedFacebookLogin(context: ComponentActivity){
        initiateFacebookLogin(context)
    }

    protected fun initiateFacebookLogin(context: ComponentActivity){
        viewModelScope.launch {

        }
    }
    fun fetchFoodAppToken(token: String,provider: String,onError:(String)->Unit){
        viewModelScope.launch {
            val request = OAuthRequest(
                token = token,
                provider = provider
            )
            val res= safeApiCall {
                foodApi.oAuth(request)
            }
            when(res){
                is ApiResponse.Success -> {
                    success(res.data.token)
                }
                else ->{
                    val error= (res as? ApiResponse.Error)?.code
                    if(error!=null){
                        when(error){
                            401 -> error("Invalid token")
                            500 -> error("Internal server error")
                            404 -> error("Not found")
                            else -> error("Something went wrong")
                        }
                    }else{
                        error("Something went wrong")
                    }
                }

            }

        }
    }
    protected fun initiateGoogleLogin(context: ComponentActivity){
        viewModelScope.launch {
                loading()
                val response = googleAuthUiProvider.signIn(
                    context,
                    CredentialManager.create(context)
                )

                fetchFoodAppToken(response.token, provider = "google"){
                    error(it)
                }
        }
    }
}