package com.example.foodhub_app.data.model

data class CartResponse(
    val checkoutDetails: CheckoutDetails,
    val items: List<CartItem>
)