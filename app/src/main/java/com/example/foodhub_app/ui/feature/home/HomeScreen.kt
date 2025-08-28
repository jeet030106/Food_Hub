package com.example.foodhub_app.ui.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodhub_app.data.model.Category
import com.example.foodhub_app.data.remote.ApiResponse
import com.example.foodhub_app.ui.theme.Orange
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.format.TextStyle


@Composable
fun HomeScreen(navController: NavController,viewModel: HomeScreenViewModel= hiltViewModel()) {
    val uiState=viewModel.uiState.collectAsState()
   Column {
       when(uiState.value){
           is HomeScreenViewModel.HomeScreenState.Success->{
               val categories=viewModel.categories
               CategoryList(categories = categories, onCategorySelected = {
                   navController.navigate("category/${it.id}")
               })
           }
           is HomeScreenViewModel.HomeScreenState.Empty->{
               Text(text = "No Data")
           }
           is HomeScreenViewModel.HomeScreenState.Loading -> {
               Text(text = "Loading")
           }
       }
   }
}

@Composable
fun CategoryList(categories:List<Category>,onCategorySelected:(Category)->Unit){

    LazyRow(
        modifier = Modifier.padding(top = 32.dp)
    ) {
        items(categories){
            CategoryItem(category = it, onCategorySelected = onCategorySelected)
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