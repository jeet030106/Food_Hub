package com.example.foodhub_app.ui.feature.order_success

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodhub_app.ui.navigation.Home
import com.example.foodhub_app.ui.theme.Primary

@Composable
fun OrderSuccessScreen(navController: NavController,orderId:String) {
    BackHandler {
        navController.popBackStack(route= Home, inclusive = false)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Order Success",style= MaterialTheme.typography.titleLarge)
        Text("Your order $orderId is successfully placed",style= MaterialTheme.typography.bodyLarge,color = Color.Gray)
        Button(onClick = { navController.popBackStack(route= Home, inclusive = false) }, colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
            Text("Continue Shopping",color = Color.White)
        }
    }
}