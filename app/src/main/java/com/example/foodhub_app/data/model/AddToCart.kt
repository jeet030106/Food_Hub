package com.example.foodhub_app.data.model

data class AddToCart(
    val restaurantId: String,
    val menuItemId: String?,
    val quantity: Int
)
