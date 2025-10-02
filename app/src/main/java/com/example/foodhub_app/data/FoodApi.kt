package com.example.foodhub_app.data

import com.example.foodhub_app.data.model.AddToCart
import com.example.foodhub_app.data.model.AddToCartResponse
import com.example.foodhub_app.data.model.Address
import com.example.foodhub_app.data.model.AddressResponse
import com.example.foodhub_app.data.model.AuthResponse
import com.example.foodhub_app.data.model.CartResponse
import com.example.foodhub_app.data.model.CategoriesResponse
import com.example.foodhub_app.data.model.ConfirmPaymentRequest
import com.example.foodhub_app.data.model.ConfirmPaymentResponse
import com.example.foodhub_app.data.model.FoodItemResponse
import com.example.foodhub_app.data.model.GenericResponse
import com.example.foodhub_app.data.model.OAuthRequest
import com.example.foodhub_app.data.model.OrderListResponse
import com.example.foodhub_app.data.model.PaymentIntentRequest
import com.example.foodhub_app.data.model.PaymentIntentResponse
import com.example.foodhub_app.data.model.RestaurantsResponse
import com.example.foodhub_app.data.model.ReverseGeocodeRequest
import com.example.foodhub_app.data.model.SignInRequest
import com.example.foodhub_app.data.model.SignUpRequest
import com.example.foodhub_app.data.model.UpdateCartItemRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApi {
    @GET("/categories")
    suspend fun getCategories(): Response<CategoriesResponse>

    @GET("/restaurants")
    suspend fun getRestaurants(@Query ("lat") lat:Double, @Query ("lon") lon:Double): Response<RestaurantsResponse>

    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>

    @POST("/auth/login")
    suspend fun signIn(@Body request: SignInRequest): Response<AuthResponse>
    @POST("/auth/oauth")
    suspend fun oAuth(@Body request: OAuthRequest): Response<AuthResponse>

    @GET("/restaurants/{restaurantId}/menu")
    suspend fun getRestaurantFoodItems(@Path ("restaurantId") restaurantId: String): Response<FoodItemResponse>

    @POST("/cart")
    suspend fun addToCart(@Body request: AddToCart):Response<AddToCartResponse>

    @GET("/cart")
    suspend fun getCart():Response<CartResponse>

    @PATCH("/cart")
    suspend fun updateCart(@Body request: UpdateCartItemRequest):Response<GenericResponse>

    @DELETE("/cart/{cartItemId}")
    suspend fun removeCartItem(@Path("cartItemId") cartItemId: String):Response<GenericResponse>

    @GET("/addresses")
    suspend fun getAddresses():Response<AddressResponse>

    @POST("/addresses/reverse-geocode")
    suspend fun reverseGeocode(@Body request: ReverseGeocodeRequest):Response<Address>

    @POST("/payments/create-intent")
    suspend fun getPaymentIntent(@Body request: PaymentIntentRequest):Response<PaymentIntentResponse>

    @POST("payments/confirm/{paymentIntentId}")
    suspend fun verifyPurchase(
        @Body request: ConfirmPaymentRequest,
        @Path("paymentIntentId") paymentIntentId: String
    ): Response<ConfirmPaymentResponse>

    @GET("/orders")
    suspend fun getOrders():Response<OrderListResponse>


}
