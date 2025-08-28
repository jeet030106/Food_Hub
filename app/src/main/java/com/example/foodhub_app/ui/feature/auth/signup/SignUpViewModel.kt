package com.example.foodhub_app.ui.feature.auth.signup

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.FoodHubSession
import com.example.foodhub_app.data.model.SignUpRequest
import com.example.foodhub_app.data.remote.ApiResponse
import com.example.foodhub_app.data.remote.safeApiCall
import com.example.foodhub_app.ui.BaseAuthViewModel
import com.example.foodhub_app.ui.feature.auth.AuthViewModel.SignAuthNavigation
import com.example.foodhub_app.ui.feature.auth.login.SignInViewModel.SignInEvent
import com.example.foodhub_app.ui.feature.auth.login.SignInViewModel.SignInNavigation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(override val foodApi: FoodApi,val session: FoodHubSession): BaseAuthViewModel(foodApi) {
    private var _uiState= MutableStateFlow<SignUpEvent>(SignUpEvent.Nothing)
    val uiState=_uiState.asStateFlow()

    private var _navigationEvent= MutableSharedFlow<SignUpNavigation>()
    val navigationEvent=_navigationEvent.asSharedFlow()

    private var _mail= MutableStateFlow("")
    val mail=_mail.asStateFlow()

    private var _name= MutableStateFlow("")
    val name=_name.asStateFlow()

    private var _password= MutableStateFlow("")
    val password=_password.asStateFlow()
    fun onMailChange(mail:String){
        _mail.value=mail
    }
    fun onNameChange(name:String){
        _name.value=name
    }
    fun onPasswordChange(password:String){
        _password.value=password
    }

    fun onSignUpClick(){
        viewModelScope.launch{
           try {
               _uiState.value=SignUpEvent.Loading
               val respose= safeApiCall {
                   foodApi.signUp(
                       SignUpRequest(
                           name=name.value,
                           email = mail.value,
                           password = password.value
                       )
                   )
               }
               when(respose){
                   is ApiResponse.Success->{
                       _uiState.value=SignUpEvent.Success
                       session.storeToken(respose.data.token)
                       _navigationEvent.emit(SignUpNavigation.NavigateToHome)
                   }
                   else -> {
                       val err=(respose as? ApiResponse.Error)?.code
                       errorTitle="Sign In Failure"
                       errorMsg="Failed to Sign In"
                       when(err){
                           401 ->{
                               errorTitle="Invalid credentials"
                               errorMsg="Please enter valid credentials"
                           }
                           500 ->{
                               errorTitle="Internal server error"
                               errorMsg="Please try again later"
                           }
                           404 ->{
                               errorTitle="Not found"
                               errorMsg="Please try again later"
                           }
                           else ->{
                               errorTitle="Something went wrong"
                               errorMsg="Please try again later"
                           }
                       }
                       _uiState.value=SignUpEvent.Error
                   }
               }
           }catch(e:Exception){
               e.printStackTrace()
               _uiState.value=SignUpEvent.Error
           }

        }
        fun onGoogleClicked(context: Context) {
            initiateGoogleLogin(context as ComponentActivity)
        }



    }


    fun onLoginChaged() {
        viewModelScope.launch {
            _navigationEvent.emit(SignUpNavigation.NavigateToLogin)
        }
    }
    override fun loading() {
        viewModelScope.launch {
            _uiState.value=SignUpEvent.Loading
        }
    }

    override fun success(token: String) {
        viewModelScope.launch {
            session.storeToken(token)
            _uiState.value=SignUpEvent.Success
            _navigationEvent.emit(SignUpNavigation.NavigateToHome)
        }
    }

    override fun error(message: String) {
        viewModelScope.launch {
            _uiState.value=SignUpEvent.Error
            _navigationEvent.emit(SignUpNavigation.NavigateToDialog)
            errorMsg=message
            errorTitle="Falied to Sign In"
        }
    }
    sealed class SignUpNavigation{
        object NavigateToLogin:SignUpNavigation()
        object NavigateToHome:SignUpNavigation()
        object NavigateToDialog:SignUpNavigation()
    }
    sealed class SignUpEvent{
        object Nothing:SignUpEvent()
        object Loading: SignUpEvent ()
        object Success:SignUpEvent()
        object Error: SignUpEvent ()
    }
}