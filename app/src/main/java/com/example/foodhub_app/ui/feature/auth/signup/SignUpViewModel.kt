package com.example.foodhub_app.ui.feature.auth.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodhub_app.data.FoodApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(val foodApi: FoodApi): ViewModel() {
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
            _uiState.value=SignUpEvent.Loading
            delay(2000)
            _uiState.value=SignUpEvent.Success
            _navigationEvent.tryEmit(SignUpNavigation.NavigateToHome)
        }



    }
    sealed class SignUpNavigation{
        object NavigateToLogin:SignUpNavigation()
        object NavigateToHome:SignUpNavigation()
    }
    sealed class SignUpEvent{
        object Nothing:SignUpEvent()
        object Loading: SignUpEvent ()
        object Success:SignUpEvent()
        object Error: SignUpEvent ()
    }
}