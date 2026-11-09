package com.example.foodhub_app.ui.feature.food_item_details

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodhub_app.R
import com.example.foodhub_app.data.model.FoodItem
import com.example.foodhub_app.ui.feature.restaurant_details.RestaurantDetail
import com.example.foodhub_app.ui.feature.restaurant_details.RestaurantHeader
import com.example.foodhub_app.ui.navigation.Cart
import com.example.foodhub_app.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.FoodDetailsScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    foodItem: FoodItem,
    viewModel: FoodDetailsViewModel = hiltViewModel()
) {
    var showDialogSuccess by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()
    val quantity by viewModel.quantity.collectAsStateWithLifecycle()
    val loading = uiState is FoodDetailsViewModel.FoodDetailsUiState.Loading

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is FoodDetailsViewModel.FoodDetailsEvent.onAddToCart -> {
                    showDialogSuccess = true
                }
                is FoodDetailsViewModel.FoodDetailsEvent.goToCart -> {
                    navController.navigate(Cart)
                }
                is FoodDetailsViewModel.FoodDetailsEvent.showErrorDialog -> {
                    Toast.makeText(
                        navController.context,
                        event.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp) // Space for sticky bottom button
        ) {
            // 🔹 Hero Header Image
            RestaurantHeader(
                imageUrl = foodItem.imageUrl,
                onFavouriteButton = {},
                animatedVisibilityScope = animatedVisibilityScope,
                restaurantId = foodItem.id,
                onBackButton = { navController.popBackStack() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔹 Food Name, Rating & Description
            RestaurantDetail(
                name = foodItem.name,
                description = foodItem.description
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 🔹 Price and Quantity Counter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Price Display
                Text(
                    text = "$${foodItem.price}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Primary
                )

                // Quantity Counter Stepper
                FoodItemCounter(
                    quantity = quantity,
                    incrementQuantity = { viewModel.incrementQuantity() },
                    decrementQuantity = { viewModel.decrementQuantity() }
                )
            }
        }

        // 🔹 Sticky "Add to Cart" Button at Bottom
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = Color.White,
            shadowElevation = 12.dp
        ) {
            Button(
                onClick = { viewModel.addToCart(
                    restaurantId = foodItem.restaurantId,
                    menuItemId = foodItem.id
                ) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(visible = !loading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.add_to_cart),
                                contentDescription = "Add to Cart Icon",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Add to Cart",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    AnimatedVisibility(visible = loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
    }

    // 🔹 Bottom Sheet Dialog on Success
    if (showDialogSuccess) {
        ModalBottomSheet(
            onDismissRequest = { showDialogSuccess = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = Primary.copy(alpha = 0.12f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(R.drawable.add_to_cart),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Added to Cart!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${foodItem.name} has been added to your shopping cart.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        showDialogSuccess = false
                        viewModel.goToCart()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text(
                        text = "Go to Cart",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { showDialogSuccess = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Primary)
                ) {
                    Text(
                        text = "Continue Shopping",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// 🔹 Compact Counter Stepper Component
@Composable
fun FoodItemCounter(
    quantity: Int,
    incrementQuantity: () -> Unit,
    decrementQuantity: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.remove),
            contentDescription = "Remove item",
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable { decrementQuantity() }
        )

        Text(
            text = "$quantity",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )

        Image(
            painter = painterResource(R.drawable.add),
            contentDescription = "Add item",
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable { incrementQuantity() }
        )
    }
}