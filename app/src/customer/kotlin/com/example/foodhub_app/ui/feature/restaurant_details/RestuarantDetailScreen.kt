package com.example.foodhub_app.ui.feature.restaurant_details

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.foodhub_app.R
import com.example.foodhub_app.data.model.FoodItem
import com.example.foodhub_app.ui.navigation.FoodDetails
import com.example.foodhub_app.ui.theme.Primary

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantDetailScreen(
    navController: NavController,
    name: String,
    imageUrl: String,
    restaurantId: String,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: RestaurantViewModel = hiltViewModel()
) {
    LaunchedEffect(restaurantId) {
        viewModel.getFoodItems(restaurantId)
    }

    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 🔹 Restaurant Cover Image Header
        item {
            RestaurantHeader(
                imageUrl = imageUrl,
                onFavouriteButton = {},
                animatedVisibilityScope = animatedVisibilityScope,
                restaurantId = restaurantId,
                onBackButton = { navController.popBackStack() }
            )
        }

        // 🔹 Restaurant Description & Details
        item {
            RestaurantDetail(
                name = name,
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
            )
        }

        // 🔹 Menu Items Section State Handling
        when (val state = uiState) {
            is RestaurantViewModel.RestaurantEvent.Loading -> {
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp)
                    ) {
                        CircularProgressIndicator(color = Primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Loading Menu...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            is RestaurantViewModel.RestaurantEvent.Error -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Failed to load menu items",
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            is RestaurantViewModel.RestaurantEvent.Success -> {
                items(state.foodItem, key = { it.id!! }) { item ->
                    FoodItems(
                        foodItem = item,
                        animatedVisibilityScope = animatedVisibilityScope,
                        onClick = { navController.navigate(FoodDetails(it)) }
                    )
                }
            }
            is RestaurantViewModel.RestaurantEvent.Nothing -> {}
        }
    }
}

// 🔹 Enhanced Food Item Card (Primary-Themed Border & Price Badge)
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodItems(
    foodItem: FoodItem,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: (FoodItem) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = Primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick(foodItem) },
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 🔹 Food Thumbnail Image Container
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                AsyncImage(
                    model = foodItem.imageUrl,
                    contentDescription = foodItem.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .sharedElement(
                            state = rememberSharedContentState(key = "image/${foodItem.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    contentScale = ContentScale.Crop
                )

                // Original Floating Favorite Button Overlay on Item Image
                IconButton(
                    onClick = { /* Handle favorite click */ },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .size(32.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.favourite_icon),
                        contentDescription = "Favorite"
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 🔹 Food Title, Price, & Description Layout
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Item Name
                    Text(
                        text = foodItem.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // 🔹 Primary-Themed Price Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Primary.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = Primary.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = "$${foodItem.price}",
                            color = Primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Item Description
                Text(
                    text = foodItem.description,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// 🔹 Restaurant Details Section
@Composable
fun RestaurantDetail(
    name: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Text(
            text = name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Rating & Review Link Row
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Rating Star",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFFFFC107)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "4.5",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "(30+)",
                fontSize = 13.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(12.dp))
            TextButton(
                onClick = {},
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "View all reviews",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Primary
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = description,
            fontSize = 13.sp,
            color = Color(0xFF4B5563),
            lineHeight = 18.sp
        )
    }
}

// 🔹 Header with Original Back & Favorite Button Styles Restored
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.RestaurantHeader(
    imageUrl: String,
    onFavouriteButton: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    restaurantId: String?,
    onBackButton: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .sharedElement(
                    state = rememberSharedContentState(key = "image/${restaurantId}"),
                    animatedVisibilityScope = animatedVisibilityScope
                ),
            contentScale = ContentScale.Crop
        )

        // Original Floating Back Button
        IconButton(
            onClick = onBackButton,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
                .size(56.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.back_button),
                contentDescription = "Back"
            )
        }

        // Original Floating Favorite Button
        IconButton(
            onClick = onFavouriteButton,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(56.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.favourite_icon),
                contentDescription = "Favorite"
            )
        }
    }
}