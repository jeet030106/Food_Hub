package com.example.foodhub_app.ui.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodhub_app.data.remote.ApiResponse

@Composable
fun HomeScreen(navController: NavController,viewModel: HomeScreenViewModel= hiltViewModel()) {

    val uiState=viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()){
        when(uiState.value){
            is HomeScreenViewModel.states.loading->{
                CircularProgressIndicator()
            }
            is HomeScreenViewModel.states.success->{

                Column (modifier = Modifier.fillMaxSize()){
                    val restaurant=(uiState.value as HomeScreenViewModel.states.success).data
                    AsyncImage(model=restaurant.imageUrl, contentDescription = null,
                        modifier = Modifier.fillMaxWidth(),
                        alignment = Alignment.Center,
                        contentScale = ContentScale.Crop)
                    Text("${restaurant.name}")
                    Text("${restaurant.createdAt}")
                }
            }
            else->{
                Text("There is an Error,, Try Again!!")
            }
        }
    }
}