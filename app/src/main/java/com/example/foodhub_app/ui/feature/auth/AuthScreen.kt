package com.example.foodhub_app.ui.feature.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.foodhub_app.R
import com.example.foodhub_app.ui.navigation.Home
import com.example.foodhub_app.ui.navigation.Login
import com.example.foodhub_app.ui.navigation.SignUp
import com.example.foodhub_app.ui.theme.BasicDialogBox
import com.example.foodhub_app.ui.theme.GroupSocialIcons
import com.example.foodhub_app.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class) // <-- Add this annotation
@Composable
fun AuthScreen(navController: NavController,viewModel: AuthViewModel = hiltViewModel()){
    val sheetState= rememberModalBottomSheetState()
    val scope= rememberCoroutineScope()
    var showDialog by remember{mutableStateOf(false)}
    val imageSize= remember {
        mutableStateOf(IntSize.Zero)
    }
    val brush= Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            Color.Black
        ),
        startY = imageSize.value.height.toFloat()/3
    )

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest {
            when(it){
                AuthViewModel.SignAuthNavigation.NavigateToHome -> {
                    navController.navigate(Home)
                }
                AuthViewModel.SignAuthNavigation.NavigateToSignUp -> {
                    navController.navigate(SignUp)
                }
                AuthViewModel.SignAuthNavigation.NavigateToDialog -> {
                    showDialog=true
                }
            }
        }
    }

    Box(modifier= Modifier
        .fillMaxSize()
        .background(Color.Black)
    ){
        Image(painter = painterResource(id = R.drawable.backgorund), contentDescription =null,
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    imageSize.value = it.size
                }
                .alpha(0.6f))
        Box(modifier = Modifier
            .matchParentSize()
            .background(brush)
        )
        Button(onClick = { /*TODO*/ },
            colors= ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)

        ) {
            Text(text= stringResource(id = R.string.skip), color = Primary)
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 110.dp)
            .padding(16.dp),
        ){
            Text(
                text = stringResource(id = R.string.welcome),
                color = Color.Black,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(id = R.string.foodhub),
                color = Primary,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.food_hub_desc),
                color = Color.DarkGray,
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ){
            GroupSocialIcons(color = Color.White,viewModel = viewModel)
            Spacer(modifier = Modifier.size(10.dp))
            Button(
                onClick = {
                    navController.navigate(SignUp)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text(text = stringResource(id = R.string.signIn), color = Color.White)
            }
            Spacer(modifier = Modifier.size(10.dp))
            TextButton(onClick = {
                navController.navigate(Login)
            }) {
                Text(text = stringResource(id = R.string.already_have_acc),
                    color = Color.White)
            }
        }
    }
    if(showDialog){
        ModalBottomSheet(onDismissRequest = { showDialog=false },sheetState = sheetState) {
            BasicDialogBox(
                viewModel.errorTitle,
                viewModel.errorMsg,
            ) {
                scope.launch {
                    sheetState.hide()
                    showDialog=false
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun View() {
    AuthScreen(rememberNavController())
}