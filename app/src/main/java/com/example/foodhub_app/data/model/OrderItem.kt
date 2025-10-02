package com.example.foodhub_app.data.model

data class OrderItem(
    val id: String,
    val menuItemId: String,
    val menuItemName: String,
    val orderId: String,
    val quantity: Int
)