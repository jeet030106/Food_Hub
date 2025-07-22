package com.example.foodhub_app.data

import retrofit2.http.GET

interface FoodApi {
    @GET("food/")
    suspend fun getFoodItems(): List<String>

}