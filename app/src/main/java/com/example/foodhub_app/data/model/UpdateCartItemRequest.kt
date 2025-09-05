package com.example.foodhub_app.data.model

data class UpdateCartItemRequest(
    val cartItemId:String,
    val quantity:Int
)