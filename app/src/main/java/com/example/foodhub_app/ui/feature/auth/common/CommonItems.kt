package com.example.foodhub_app.ui.feature.auth.common

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil.compose.AsyncImage
import com.example.foodhub_app.R
import com.example.foodhub_app.data.model.FoodItem

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FoodItems(foodItem: FoodItem,animatedVisibilityScope: AnimatedVisibilityScope,onClick:(FoodItem)->Unit) {
    Box(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .height(128.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .border(
                color = MaterialTheme.colorScheme.primary,
                width = 1.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable{onClick.invoke(foodItem)}
            .sharedElement(state=rememberSharedContentState(key="image/${foodItem.id}"),animatedVisibilityScope)
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // This is the Box for the Image and Favorite Icon
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = foodItem.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { /* Handle favorite click */ },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .size(32.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.favourite_icon),
                        contentDescription = "Favorite",
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            // ⭐ START OF CHANGES: Main container for all text
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                // 1. Create a Row for the name and price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // Pushes items to ends
                ) {
                    // Item Name
                    Text(
                        text = foodItem.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f) // Takes up available space
                    )

                    // Price Box (MOVED HERE)
                    Box(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(32.dp)
                            .width(64.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Text(
                            text = "$${foodItem.price}",
                            color = Color.Black,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.size(4.dp))

                // 2. Description is placed below the Row
                Text(
                    text = foodItem.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2, // Prevents overlapping if description is long
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}