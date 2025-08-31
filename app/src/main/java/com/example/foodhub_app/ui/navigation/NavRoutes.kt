package com.example.foodhub_app.ui.navigation

import com.example.foodhub_app.data.model.FoodItem
import kotlinx.serialization.Serializable

interface navRoutes

@Serializable
object SignUp:navRoutes

@Serializable
object Login:navRoutes

@Serializable
object Auth:navRoutes

@Serializable
object Home:navRoutes

@Serializable
data class RestaurantDetail(val restaurantId:String,val name:String,val imageUrl:String):navRoutes

@Serializable
data class FoodDetails(val foodItem: FoodItem):navRoutes

