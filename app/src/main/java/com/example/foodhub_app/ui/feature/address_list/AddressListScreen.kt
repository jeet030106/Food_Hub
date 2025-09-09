package com.example.foodhub_app.ui.feature.address_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.foodhub_app.R
import com.example.foodhub_app.data.model.Address

@Composable
fun AddressListScreen(navController: NavHostController,viewModel: AddressListViewModel= hiltViewModel()){
    val uiState=viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(true) {
        when(viewModel._navigationEvent){
            is AddressListViewModel.AddressEvents.NavigateToEditAddress ->{

            }

            is AddressListViewModel.AddressEvents.NavigateToAddAddress ->{

            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(painter = painterResource(id = R.drawable.back_button), contentDescription = null,modifier = Modifier.size(48.dp).clickable{navController.popBackStack()})
            Spacer(modifier = Modifier.size(16.dp))
            Text(text = "Address", style = MaterialTheme.typography.titleLarge)
        }

        Spacer(modifier = Modifier.size(16.dp))

        when(val address=uiState.value){
            is AddressListViewModel.AddressStates.Loading->{
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    CircularProgressIndicator()
                    Text(text = "Loading...")
                }
            }
            is AddressListViewModel.AddressStates.Success->{
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(address.addresses){address->
                        AddressItem(address = address)
                    }
                }

            }

            is AddressListViewModel.AddressStates.Error -> {

            }
        }
    }
}

@Composable
fun AddressItem(address: Address){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .shadow(elevation = 4.dp,shape=RoundedCornerShape(12.dp))
            .clickable{}
    ){
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(text = address.addressLine1,style=MaterialTheme.typography.bodyLarge,color=Color.Black)
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = address.addressLine2.toString(),style=MaterialTheme.typography.bodyMedium,color=Color.Gray)
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = "${address.city}, ${address.state}, ${address.country}",style=MaterialTheme.typography.bodyMedium,color=Color.Gray)
        }
    }
}