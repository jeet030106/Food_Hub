package com.example.foodhub_app.ui

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.auth.GoogleAuthUiProvider
import com.example.foodhub_app.data.model.OAuthRequest
import com.example.foodhub_app.ui.feature.auth.login.SignInViewModel.SignInEvent
import com.example.foodhub_app.ui.feature.auth.login.SignInViewModel.SignInNavigation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class BaseAuthViewModel(open val foodApi: FoodApi): ViewModel() {
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

    protected fun initiateGoogleLogin(context: ComponentActivity){
        viewModelScope.launch {
            try { // Add a try-catch block to handle exceptions
                loading()
                val response = googleAuthUiProvider.signIn(
                    context,
                    CredentialManager.create(context)
                )

                if (response != null) {
                    val request = OAuthRequest(
                        token = response.token,
                        provider = "google"
                    )
                    val res = foodApi.oAuth(request)
                    if (res.token.isNotEmpty()) {
                        Log.d("SignInViewModel", "onGoogleClicked: ${res.token}")
                        success(res.token)
                    } else {
                        error("Invalid token")
                    }
                } else {
                    // User might have cancelled the sign-in
                    error("User might have cancelled the sign-in")
                }
            } catch (e: Exception) {
                // This will catch crashes from the signIn process or the api call
                e.printStackTrace()
                error("Something went wrong")
            }
        }
    }
}