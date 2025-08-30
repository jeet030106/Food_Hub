package com.example.foodhub_app.ui.feature.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodhub_app.R
import com.example.foodhub_app.data.model.Category
import com.example.foodhub_app.data.model.Restaurants
import com.example.foodhub_app.data.remote.ApiResponse
import com.example.foodhub_app.ui.navigation.RestaurantDetail
import com.example.foodhub_app.ui.theme.Orange
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.format.TextStyle


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.HomeScreen(navController: NavController,animatedVisibilityScope: AnimatedVisibilityScope,viewModel: HomeScreenViewModel= hiltViewModel()) {

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest {
            when(it){
                is HomeScreenViewModel.HomeScreenNavigationEvents.NavigateToDetail->{
                    navController.navigate(RestaurantDetail(
                        it.restaurantId, it.name, it.imageUrl
                    ))
                }
                else->{

                }
            }
        }
    }
    val uiState = viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.001f)).padding(16.dp)) {
        when (uiState.value) {
            is HomeScreenViewModel.HomeScreenState.Success -> {
                val categories = viewModel.categories
                val restaurants = viewModel.restaurants

                // Categories
                CategoryList(categories = categories, onCategorySelected = {
                    navController.navigate("category/${it.id}")
                })

                Spacer(modifier = Modifier.height(24.dp))

                // Restaurants
                RestaurantList(restaurants = restaurants, animatedVisibilityScope,onRestaurantSelected = {
                    viewModel.onRestaurantSelected(it)
                })
            }

            is HomeScreenViewModel.HomeScreenState.Empty -> {
                Log.d("Jeet", "Data not fetched")
            }

            is HomeScreenViewModel.HomeScreenState.Loading -> {
                Log.d("Jeet", "Data loading")
            }
        }
    }
}
@Composable
fun CategoryList(categories:List<Category>,onCategorySelected:(Category)->Unit){
    Column {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "What would you like to eat?",
            // Remove .size(16.dp) from here
            modifier = Modifier
                .fillMaxWidth() // Optional: makes it span the width
                .padding(horizontal = 16.dp),
            color = Color.Black,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold// Optional: set a specific font size
        )
        LazyRow(
            modifier = Modifier.padding(top = 32.dp)
        ) {
            items(categories){
                CategoryItem(category = it, onCategorySelected = onCategorySelected)
            }
        }
    }

}
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantList(restaurants:List<Restaurants>,animatedVisibilityScope: AnimatedVisibilityScope, onRestaurantSelected:(Restaurants)->Unit){
    Column() {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Text(text = "Popular Restaurants",
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                ),
                color = Color.Black
            )
            TextButton(onClick = { }) {
                Text(text = "View All",
                    style = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    ),
                    color = Orange
                )
            }
        }
        LazyRow(
            modifier = Modifier.padding(top = 8.dp)
        ) {
            items(restaurants){
                RestaurantItem(it,onRestaurantSelected, animatedVisibilityScope)
            }
        }
    }

}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantItem(restaurants: Restaurants, onRestaurantSelected: (Restaurants) -> Unit,animatedVisibilityScope: AnimatedVisibilityScope) {
    Box(
        modifier = Modifier
            .width(272.dp)
            .height(252.dp)
            .padding(8.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // 🔹 Image + Rating overlay
            Box(modifier = Modifier
                .fillMaxWidth()
                .weight(7.5f)
            ) {
                AsyncImage(
                    model = restaurants.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().sharedElement(state= rememberSharedContentState(key="image/${restaurants.id}"),animatedVisibilityScope),
                    contentScale = ContentScale.Crop
                )

                // Rating row overlaid on top-left of image
                Row(
                    modifier = Modifier
                        .align(TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "4.5",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black,
                        modifier = Modifier
                            .padding(end = 4.dp)
                    )
                    Image(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        colorFilter = ColorFilter.tint(Color.Yellow)
                    )
                    Text(
                        text = "(25+)",
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 12.sp
                        ),
                        color = Color.Black.copy(alpha = 0.8f),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }

            // 🔹 Restaurant Info Box
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onRestaurantSelected(restaurants) }
                    .clip(RoundedCornerShape(8.dp))
                    .weight(2.5f)
            ) {
                Column {
                    Text(
                        text = restaurants.name,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.Black
                    )
                    Row {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.delivery),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(12.dp)
                            )
                            Text(
                                text = "Free Delivery",
                                fontSize = 12.sp,
                                color = Color.Gray.copy(alpha = 0.8f)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(R.drawable.timer),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .size(12.dp)
                            )
                            Text(
                                text = "10-15",
                                fontSize = 12.sp,
                                color = Color.Gray.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryItem(category:Category,onCategorySelected:(Category)->Unit){
    Column(
        modifier = Modifier
            .padding(8.dp)
            .height(90.dp)
            .width(60.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(45.dp),
                ambientColor = Color.Gray.copy(alpha = 0.8f),
                spotColor = Color.Gray.copy(alpha = 0.8f)
            )
            .clickable {onCategorySelected(category)}
            .background(color = Color.White)
            .clip(RoundedCornerShape(45.dp))
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = category.imageUrl,
            contentDescription = category.name,
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
                .shadow(
                    elevation = 16.dp,
                    shape = CircleShape,
                    ambientColor = Orange,
                    spotColor = Orange
                ),
            contentScale = ContentScale.Inside,
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = category.name,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        )
    }
}