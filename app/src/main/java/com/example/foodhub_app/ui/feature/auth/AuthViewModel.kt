package com.example.foodhub_app.ui.feature.auth

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewModelScope
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.model.OAuthRequest
import com.example.foodhub_app.data.model.SignInRequest
import com.example.foodhub_app.ui.BaseAuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AuthViewModel@Inject constructor(override val foodApi: FoodApi): BaseAuthViewModel(foodApi) {
    private var _uiState= MutableStateFlow<SignAuthEvent>(SignAuthEvent.Nothing)
    val uiState=_uiState.asStateFlow()

    private var _navigationEvent= MutableSharedFlow<SignAuthNavigation>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    fun onGoogleClicked(context: Context) {
        initiateGoogleLogin(context as ComponentActivity)
    }

    override fun loading() {
        viewModelScope.launch {
            _uiState.value=SignAuthEvent.Loading
        }
    }

    override fun success(token: String) {
        viewModelScope.launch {
            _uiState.value=SignAuthEvent.Success
            _navigationEvent.emit(SignAuthNavigation.NavigateToHome)
        }
    }

    override fun error(message: String) {
        viewModelScope.launch {
            errorMsg=message
            errorTitle="Falied to Sign In"
            _navigationEvent.emit(SignAuthNavigation.NavigateToDialog)
            _uiState.value=SignAuthEvent.Error
        }
    }

    sealed class SignAuthNavigation{
        object NavigateToSignUp:SignAuthNavigation()
        object NavigateToHome:SignAuthNavigation()
        object NavigateToDialog:SignAuthNavigation()
    }
    sealed class SignAuthEvent{
        object Nothing:SignAuthEvent()
        object Loading: SignAuthEvent ()
        object Success:SignAuthEvent()
        object Error: SignAuthEvent ()
    }
}