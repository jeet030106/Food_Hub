package com.example.foodhub_app.data.model

data class Order(
    val address: Address,
    val createdAt: String,
    val id: String,
    val items: List<OrderItem>,
    val paymentStatus: String,
    val restaurant: Restaurants,
    val restaurantId: String,
    val riderId: Any,
    val status: String,
    val stripePaymentIntentId: String,
    val totalAmount: Double,
    val updatedAt: String,
    val userId: String
)