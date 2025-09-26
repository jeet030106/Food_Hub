package com.example.foodhub_app.data.model

data class ConfirmPaymentRequest(
    val paymentIntentId:String,
    val addressId: String,
    val paymentMethodId: String? = null
)
