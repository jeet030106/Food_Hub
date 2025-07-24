package com.example.foodhub_app.ui.feature.auth.signup

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodhub_app.R
import com.example.foodhub_app.ui.theme.FoodHubTextField
import com.example.foodhub_app.ui.theme.GroupSocialIcons
import com.example.foodhub_app.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest


@Composable
fun SignUpScreen(viewModel: SignUpViewModel= hiltViewModel()){
    val name=viewModel.name.collectAsStateWithLifecycle()
    val mail=viewModel.mail.collectAsStateWithLifecycle()
    val passsword=viewModel.password.collectAsStateWithLifecycle()
    val errorMessage=remember{ mutableStateOf<String?>(null) }
    val loading= remember { mutableStateOf(false) }
    val _uiState=viewModel.uiState.collectAsState()
    when(_uiState.value){
        is SignUpViewModel.SignUpEvent.Error->{
            errorMessage.value="Failed"
            loading.value=false
        }
        is  SignUpViewModel.SignUpEvent.Loading->{
            errorMessage.value=null
            loading.value=true
        }
        else->{
            errorMessage.value=null
            loading.value=false
        }
    }
    val context= LocalContext.current
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest {
            when(it){
                is SignUpViewModel.SignUpNavigation.NavigateToHome->{
                    Toast.makeText(context,"Nav to Home", Toast.LENGTH_SHORT).show()
                }
                is SignUpViewModel.SignUpNavigation.NavigateToLogin->{

                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(id = R.drawable.ic_auth_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .align(Alignment.Center)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(id = R.string.signUp_small),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(16.dp))
                FoodHubTextField(
                    value = name.value,
                    onValueChange ={viewModel.onNameChange(it)},
                    label = {
                        Text(text = stringResource(id = R.string.full_name),
                            color = Color.Gray)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp),
                    shape = RoundedCornerShape(16.dp)

                )
                Spacer(modifier = Modifier.size(32.dp))
                FoodHubTextField(
                    value = mail.value,
                    onValueChange ={viewModel.onMailChange(it)},
                    label = {
                        Text(text = stringResource(id = R.string.mail),
                            color = Color.Gray)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.size(32.dp))
                FoodHubTextField(
                    value = passsword.value,
                    onValueChange ={viewModel.onPasswordChange(it)},
                    label = {
                        Text(text = stringResource(id = R.string.password),
                            color = Color.Gray)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(65.dp),
                    visualTransformation = PasswordVisualTransformation(),
                    trailingIcon = {
                        Image(painter = painterResource(
                            id = R.drawable.ic_eye)
                            , contentDescription = null
                        )
                    },
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.size(32.dp))
                errorMessage.value?.let { Text(text = it, color = Color.Red) }
                Button(
                    onClick = viewModel::onSignUpClick,
                    colors =ButtonDefaults.buttonColors(containerColor = Orange),
                    modifier = Modifier
                        .width(275.dp)
                        .height(64.dp)
                        .shadow(4.dp, CircleShape)
                ) {
                    Box(){
                        AnimatedContent(
                            targetState = loading.value,
                            transitionSpec ={
                                fadeIn(animationSpec = tween(300))+ scaleIn(initialScale = 0.8f) togetherWith
                                        fadeOut(animationSpec = tween(300))+ scaleOut(targetScale = 0.8f)
                            }
                        ) {
                            if (it){
                                CircularProgressIndicator(
                                    color = Color.White
                                )
                            }else{
                                Text(text = stringResource(id = R.string.signUp),
                                    fontSize=18.sp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.size(32.dp),)
                Text(
                    text = stringResource(id = R.string.already_have_acc),
                    color = Orange.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.size(48.dp))
                GroupSocialIcons(
                    color = Color.Gray,
                    onFacebookClick = { /*TODO*/ },
                    onGoogleClick = {}
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun Screen(){
    SignUpScreen()
}