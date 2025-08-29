package com.example.foodhub_app.ui.feature.restaurant_details

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodhub_app.R
@Composable
fun RestaurantDetailScreen(
    navController: NavController,
    name:String,
    imageUrl:String,
    restaurantId:String,
    viewModel: RestaurantViewModel = hiltViewModel()

){
    LaunchedEffect(restaurantId) {
        viewModel.getFoodItems(restaurantId)
    }
    val uiState=viewModel.uiState.collectAsState()
    LazyColumn {
        item {
            RestaurantHeader(imageUrl,onFavouriteButton = {},onBackButton = {navController.popBackStack()})
        }
        item{
            RestaurantDetail(name = name,description = "This is a description. Making it long")
        }
        when(uiState.value){
            is RestaurantViewModel.RestaurantEvent.Loading->{
                item{
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier=Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                    ){
                        CircularProgressIndicator()
                        Text(text="Loading")
                    }
                }
            }
            is RestaurantViewModel.RestaurantEvent.Error->{
                item{
                    Text(text="Error")
                }
            }
            is RestaurantViewModel.RestaurantEvent.Success->{
                val foodItem=
                    (uiState.value as RestaurantViewModel.RestaurantEvent.Success).foodItem
                items(foodItem){
                    Text(text=it.name)
                }
            }
            is RestaurantViewModel.RestaurantEvent.Nothing->{

            }
        }
    }
}

@Composable
fun RestaurantDetail(
    name:String,
    description:String
){
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Text(text=name,style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.size(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically){
           Icon(imageVector = Icons.Filled.Star, contentDescription = null,modifier = Modifier.size(48.dp),
               tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.size(8.dp))
            Text(text="4.5",style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(
                Alignment.CenterVertically))
            Text(text="(30+)",style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(
                Alignment.CenterVertically))
            Spacer(modifier = Modifier.size(8.dp))
            TextButton(onClick = {}) {
                Text(text="View all reviews",style = MaterialTheme.typography.bodyMedium,color=MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(modifier = Modifier.size(8.dp))
        Text(text=description,style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(8.dp))

    }
}

@Composable
fun RestaurantHeader(
    imageUrl:String,
    onFavouriteButton:()->Unit,
    onBackButton:()->Unit,
){
    Box(modifier= Modifier.fillMaxWidth()){
        AsyncImage(model=imageUrl, contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 16.dp,bottomEnd = 16.dp)),
            contentScale = ContentScale.Fit,
        )
        IconButton(onClick =onBackButton,
            modifier = Modifier.padding(8.dp).size(48.dp).align(Alignment.TopStart)
        ){
            Icon(painter = painterResource(id = R.drawable.back_button), contentDescription = null)
        }
        IconButton(onClick =onFavouriteButton,
            modifier = Modifier.padding(8.dp).size(48.dp).align(Alignment.TopEnd)
        ){
            Icon(painter = painterResource(id = R.drawable.favourite_icon), contentDescription = null)
        }
    }
}