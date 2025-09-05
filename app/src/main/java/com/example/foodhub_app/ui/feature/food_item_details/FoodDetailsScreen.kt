package com.example.foodhub_app.ui.feature.food_item_details

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodhub_app.data.model.FoodItem
import com.example.foodhub_app.ui.feature.restaurant_details.RestaurantDetail
import com.example.foodhub_app.ui.feature.restaurant_details.RestaurantHeader
import com.example.foodhub_app.R
import com.example.foodhub_app.ui.theme.Primary

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.foodhub_app.ui.navigation.Cart
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.FoodDetailsScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    foodItem: FoodItem,
    viewModel:FoodDetailsViewModel= hiltViewModel()
) {
    val showDialogSuccess=remember {
        mutableStateOf(false)
    }
    val uiState=viewModel.uiState.collectAsState()
    val quantity=viewModel.quantity.collectAsStateWithLifecycle()
    val loading=remember {
        mutableStateOf(false)
    }
    when(uiState){
        FoodDetailsViewModel.FoodDetailsUiState.Loading->{
            loading.value=true
        }
        else -> {
            loading.value=false
        }
    }
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest {
            when(it){
                is FoodDetailsViewModel.FoodDetailsEvent.onAddToCart -> {
                    showDialogSuccess.value=true
                }
                is FoodDetailsViewModel.FoodDetailsEvent.goToCart -> {
                    navController.navigate(Cart)
                }
                is FoodDetailsViewModel.FoodDetailsEvent.showErrorDialog -> {
                    Toast.makeText(
                        navController.context,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                // Add padding at the bottom so content doesn't hide behind the button
                .padding(bottom = 96.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
                    RestaurantHeader(
                        imageUrl = foodItem.imageUrl,
                        onFavouriteButton = {},
                        animatedVisibilityScope = animatedVisibilityScope,
                        restaurantId = foodItem.id
                    ) { navController.popBackStack() }

                    Spacer(modifier = Modifier.height(8.dp))
                    RestaurantDetail(name = foodItem.name, description = foodItem.description)
                    Spacer(modifier = Modifier.height(16.dp))

                    // A Row to neatly position the price and the static quantity display
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Price on the left
                        Text(
                            text = "$${foodItem.price}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        // This Spacer pushes the quantity display all the way to the right
                        Spacer(modifier = Modifier.weight(1f))

                        // Static quantity display on the right (non-functional)
                        FoodItemCounter(
                            quantity = quantity.value,
                            incrementQuantity = {viewModel.incrementQuantity()},
                            decrementQuantity = {viewModel.decrementQuantity()}
                        )
                    }
                }

                // The "Add to Cart" button is aligned to the bottom of the Box,
                // making it "sticky" and always visible.
        Button(
            onClick = { viewModel.addToCart(restaurantId = foodItem.restaurantId, foodItem.id) },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            // This outer Row is used to center the content within the Button
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                AnimatedVisibility(visible = !loading.value) {
                    // ✅ FIX: Wrap the content in its own Row to arrange it horizontally
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.add_to_cart),
                            contentDescription = "Add to Cart Icon"
                        )

                        // The original spacer was a bit wide, adjusting for better balance
                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Add to Cart",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                AnimatedVisibility(visible = loading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White, // Make the indicator visible on a colored button
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
    if(showDialogSuccess.value){
        ModalBottomSheet(onDismissRequest = { showDialogSuccess.value=false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Added To Cart")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    showDialogSuccess.value=false
                    viewModel.goToCart()
                },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ){
                    Text(text = "Go To Cart")
                }
                Button(onClick = {
                    showDialogSuccess.value=false
                },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ){
                    Text(text = "OK")
                }
            }

        }
    }
}

@Composable
fun FoodItemCounter(
    quantity: Int,
    incrementQuantity: () -> Unit,
    decrementQuantity: () -> Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.remove),
            contentDescription = "Remove item",
            modifier = Modifier.clickable{decrementQuantity()}.size(64.dp)
        )
        Text(
            text = "${quantity}", // Hardcoded value
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Image(
            painter = painterResource(R.drawable.add),
            contentDescription = "Add item",
            modifier = Modifier.clickable{incrementQuantity()}.size(64.dp)
        )
    }
}