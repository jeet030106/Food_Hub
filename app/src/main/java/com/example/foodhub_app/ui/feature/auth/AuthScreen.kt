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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.foodhub_app.R
import com.example.foodhub_app.ui.theme.GroupSocialIcons
import com.example.foodhub_app.ui.theme.Orange

@Composable
fun AuthScreen(){
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
            Text(text= stringResource(id = R.string.skip), color = Orange)
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
                color = Orange,
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
            GroupSocialIcons(color = Color.White, onFacebookClick = { /*TODO*/ }) {

            }
            Spacer(modifier = Modifier.size(10.dp))
            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text(text = stringResource(id = R.string.signIn), color = Color.White)
            }
            Spacer(modifier = Modifier.size(10.dp))
            TextButton(onClick = { /*TODO*/ }) {
                Text(text = stringResource(id = R.string.already_have_acc),
                    color = Color.White)
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun View() {
    AuthScreen()
}