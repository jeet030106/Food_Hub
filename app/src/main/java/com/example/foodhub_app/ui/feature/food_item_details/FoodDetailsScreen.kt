package com.example.foodhub_app.ui.feature.food_item_details

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.foodhub_app.data.model.FoodItem
import com.example.foodhub_app.ui.feature.restaurant_details.RestaurantDetail
import com.example.foodhub_app.ui.feature.restaurant_details.RestaurantHeader
import com.example.foodhub_app.R
import com.example.foodhub_app.ui.theme.Primary

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodDetailsScreen(
    navController: NavController,
    animatedVisibilityScope: AnimatedVisibilityScope,
    foodItem: FoodItem
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 96.dp)
        ) {
            RestaurantHeader(
                imageUrl = foodItem.imageUrl,
                onFavouriteButton = {},
                animatedVisibilityScope = animatedVisibilityScope,
                restaurantId = foodItem.id
            ) { navController.popBackStack() }

            Spacer(modifier = Modifier.height(8.dp))
            RestaurantDetail(name = foodItem.name, description = foodItem.description)
            Spacer(modifier = Modifier.height(16.dp))

            // This is the corrected Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                // 1. FIX: Use SpaceBetween to push children to opposite ends
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Price on the left
                Text(
                    text = "$${foodItem.price}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )

                // 2. FIX: The weighted Spacer is no longer needed
                // Spacer(modifier = Modifier.weight(1f))

                // Static quantity display on the right
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.remove),
                        contentDescription = "Remove item",
                        modifier = Modifier.padding(top=8.dp)
                    )
                    Text(
                        text = "1",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Image(
                        painter = painterResource(R.drawable.add),
                        contentDescription = "Add item"
                    )
                }
            }
        }

        // The "Add to Cart" button at the bottom remains the same
        Button(
            onClick = { /* No action for now */ },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.add_to_cart),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add to Cart",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}