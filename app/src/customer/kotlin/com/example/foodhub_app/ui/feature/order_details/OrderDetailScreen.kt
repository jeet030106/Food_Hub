package com.example.foodhub_app.ui.feature.order_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodhub_app.R
import com.example.foodhub_app.ui.theme.Primary

@Composable
fun OrderDetailScreen(navController: NavController,orderId:String, viewModel: OrderDetailViewModel=hiltViewModel()) {

    LaunchedEffect(key1 = orderId){
        viewModel.getOrderDetail(orderId)
    }

    LaunchedEffect(key1=true) {
        viewModel.orderDetailNav.collect {
            when(it){
                is OrderDetailViewModel.OrderDetailNav.Back -> {
                    navController.popBackStack()
                }

                null ->{

                }
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding( 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ){
            Image(painter = painterResource(id = R.drawable.back_button), contentDescription = "Back",modifier = Modifier.size(36.dp).clip(CircleShape).clickable{viewModel.onBackClick()})
            Text(text="Order Details",style=MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.size(16.dp))
        val uiStates=viewModel.orderDetailStates.collectAsStateWithLifecycle()
        when(uiStates.value){
            is OrderDetailViewModel.OrderDetailStates.Loading->{
                CircularProgressIndicator()
            }
            is OrderDetailViewModel.OrderDetailStates.Success->{
                val order=(uiStates.value as OrderDetailViewModel.OrderDetailStates.Success).order
                Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally,verticalArrangement = Arrangement.Center){
                    Text(text= order.id)
                    Text(text= order.restaurant.name)
                    Text(text= order.status)
                }
            }
            is OrderDetailViewModel.OrderDetailStates.Error->{
                Text(text="Something went wrong!")
                Button(onClick = { viewModel.getOrderDetail(orderId) },
                    modifier = Modifier.padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                    Text(text="Retry")
                }
            }

            null -> TODO()
        }
    }
}