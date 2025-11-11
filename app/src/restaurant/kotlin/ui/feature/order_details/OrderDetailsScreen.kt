package com.example.foodhub_app.ui.feature.order_details

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OrderDetailsScreen(navController: NavController,orderId:String,viewModel: OrderDetailsViewModel= hiltViewModel()){
    LaunchedEffect(true) {
        viewModel.getOrderDetails(orderId)
    }
    val uiState=viewModel.uiState.collectAsStateWithLifecycle()
    Column(modifier = Modifier.fillMaxSize()) {
        LaunchedEffect(key1 =true){
            viewModel.events.collectLatest {
                when(it){
                    is OrderDetailsViewModel.NavEvents.navigatePopUp->{
                        val msg=it.msg
                        Toast.makeText(navController.context, msg, Toast.LENGTH_SHORT).show()
                    }
                    is OrderDetailsViewModel.NavEvents.back->{
                        navController.popBackStack()
                    }

                    null -> TODO()
                }
            }
        }

        when(uiState.value){
            is OrderDetailsViewModel.States.loading->{
                CircularProgressIndicator()
            }
            is OrderDetailsViewModel.States.success->{
                val order=(uiState.value as OrderDetailsViewModel.States.success).order
                Text(order.id)
                Column(
                    modifier = Modifier.fillMaxWidth().padding(8.dp).shadow(8.dp).background(Color.White)
                ) {
                    order.items.forEach {
                        Text(it.menuItemId)
                        Text(it.quantity.toString())
                    }
                }
                FlowRow(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    viewModel.listOfStatus.forEach {
                        Button(onClick = {
                            viewModel.updateOrderStatus(orderId,it)
                        },
                            enabled = order.status != it) {
                            Text(it)
                        }
                    }
                }
            }
            else->{
                Text("There is an Error,, Try Again!!")
            }
        }
    }
}