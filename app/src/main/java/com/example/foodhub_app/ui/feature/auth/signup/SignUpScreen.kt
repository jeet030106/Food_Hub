package com.example.foodhub_app.ui.feature.auth.signup

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodhub_app.R
import com.example.foodhub_app.ui.theme.FoodHubTextField
import com.example.foodhub_app.ui.theme.GroupSocialIcons
import com.example.foodhub_app.ui.theme.Orange


@Composable
fun SignUpScreen(){

    Box(modifier = Modifier.fillMaxSize()){
        var name by remember {
            mutableStateOf("")
        }
        var mail by remember {
            mutableStateOf("")
        }
        var password by remember {
            mutableStateOf("")
        }
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
                    value = name,
                    onValueChange ={name=it},
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
                    value = mail,
                    onValueChange ={mail=it},
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
                    value = password,
                    onValueChange ={password=it},
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

                Button(
                    onClick = {},
                    colors =ButtonDefaults.buttonColors(containerColor = Orange),
                    modifier = Modifier
                        .width(275.dp)
                        .height(64.dp)
                        .shadow(4.dp, CircleShape)
                ) {
                    Text("SignUp")
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
