package com.example.foodhub_app.data.model

data class ConfirmPaymentResponse(
    val status: String,
    val requiresAction: Boolean,
    val clientSecret: String,
    val orderId: String? = null,
    val orderStatus: String? = null,
    val message: String? = null
)
