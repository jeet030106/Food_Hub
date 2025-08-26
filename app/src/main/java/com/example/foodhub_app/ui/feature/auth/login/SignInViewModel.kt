package com.example.foodhub_app.ui.feature.auth.login

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.auth.GoogleAuthUiProvider
import com.example.foodhub_app.data.model.OAuthRequest
import com.example.foodhub_app.data.model.SignInRequest
import com.example.foodhub_app.data.model.SignUpRequest
import com.example.foodhub_app.ui.feature.auth.signup.SignUpViewModel
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(val foodApi: FoodApi): ViewModel() {
    val googleAuthUiProvider= GoogleAuthUiProvider()
    private var _uiState= MutableStateFlow<SignInEvent>(SignInEvent.Nothing)
    val uiState=_uiState.asStateFlow()

    private var _navigationEvent= MutableSharedFlow<SignInNavigation>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    private var _mail= MutableStateFlow("")
    val mail=_mail.asStateFlow()

    private var _password= MutableStateFlow("")
    val password=_password.asStateFlow()
    fun onMailChange(mail:String){
        _mail.value=mail
    }
    fun onPasswordChange(password:String){
        _password.value=password
    }

    fun onSignInClick(){
        viewModelScope.launch{
            try {
                _uiState.value=SignInEvent.Loading
                val respose=foodApi.signIn(
                    SignInRequest(
                        email = mail.value,
                        password = password.value
                    )
                )
                if(respose.token.isNotEmpty()){
                    _uiState.value=SignInEvent.Success
                    _navigationEvent.emit(SignInNavigation.NavigateToHome)
                }
            }catch(e:Exception){
                e.printStackTrace()
                _uiState.value=SignInEvent.Error
            }

        }



    }


    fun onSignUpChaged() {
        viewModelScope.launch {
            _navigationEvent.emit(SignInNavigation.NavigateToSignUp)
        }
    }
    fun onGoogleClicked(context: Context) {
        viewModelScope.launch {
            _uiState.value = SignInEvent.Loading
            try { // Add a try-catch block to handle exceptions
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
                        _uiState.value = SignInEvent.Success
                        _navigationEvent.emit(SignInNavigation.NavigateToHome)
                    } else {
                        _uiState.value = SignInEvent.Error
                    }
                } else {
                    // User might have cancelled the sign-in
                    _uiState.value = SignInEvent.Error
                }
            } catch (e: Exception) {
                // This will catch crashes from the signIn process or the api call
                e.printStackTrace()
                _uiState.value = SignInEvent.Error
            }
        }
    }

    sealed class SignInNavigation{
        object NavigateToSignUp:SignInNavigation()
        object NavigateToHome:SignInNavigation()
    }
    sealed class SignInEvent{
        object Nothing:SignInEvent()
        object Loading: SignInEvent ()
        object Success:SignInEvent()
        object Error: SignInEvent ()
    }
}