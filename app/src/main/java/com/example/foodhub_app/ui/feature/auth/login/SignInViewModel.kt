package com.example.foodhub_app.ui.feature.auth.login

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.auth.GoogleAuthUiProvider
import com.example.foodhub_app.data.model.OAuthRequest
import com.example.foodhub_app.data.model.SignInRequest
import com.example.foodhub_app.data.model.SignUpRequest
import com.example.foodhub_app.ui.BaseAuthViewModel
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
class SignInViewModel @Inject constructor(override val foodApi: FoodApi): BaseAuthViewModel(foodApi) {
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
                if(respose.body()?.token?.isNotEmpty()==true){
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
        initiateGoogleLogin(context as ComponentActivity)
    }

    override fun loading() {
        viewModelScope.launch {
            _uiState.value=SignInEvent.Loading
        }
    }

    override fun success(token: String) {
        viewModelScope.launch {
            _uiState.value=SignInEvent.Success
            _navigationEvent.emit(SignInNavigation.NavigateToHome)

        }
    }

    override fun error(message: String) {
        viewModelScope.launch {
            _uiState.value=SignInEvent.Error
            errorMsg=message
            errorTitle="Falied to Sign In"
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