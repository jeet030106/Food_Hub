package com.example.foodhub_app.data

import com.example.foodhub_app.data.model.AuthResponse
import com.example.foodhub_app.data.model.OAuthRequest
import com.example.foodhub_app.data.model.SignInRequest
import com.example.foodhub_app.data.model.SignUpRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodApi {
    @GET("food/")
    suspend fun getFoodItems(): List<String>

    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse

    @POST("/auth/login")
    suspend fun signIn(@Body request: SignInRequest): AuthResponse
    @POST("/auth/oauth")
    suspend fun oAuth(@Body request: OAuthRequest): AuthResponse
}
