package com.example.foodhub_app.data

import android.content.Context
import com.example.foodhub_app.data.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideClient(session: FoodHubSession):OkHttpClient{
        val client=OkHttpClient.Builder()
        client.addInterceptor {chain->
            val request=chain.request().newBuilder()
                .addHeader("Authorization","Bearer ${session.getToken()}")
                .build()
                chain.proceed(request)
            }
            client.addInterceptor(HttpLoggingInterceptor().apply {
                level= HttpLoggingInterceptor.Level.BODY
            })
            return client.build()

    }

//    @Provides
//    fun provideOkHttpClient(): OkHttpClient {
//        val logging = HttpLoggingInterceptor()
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
//        return OkHttpClient.Builder()
//            .addInterceptor(logging)
//            .build()
//    }
    @Provides
    fun provideRetrofit(client:OkHttpClient):Retrofit{
        return Retrofit.Builder()
//            .baseUrl("http://192.168.9.202:8080")
            .baseUrl("http://10.0.2.2:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
    @Provides
    fun provideApi(retrofit: Retrofit):FoodApi{
        return retrofit.create(FoodApi::class.java)
    }

    @Provides
    fun provideSession(@ApplicationContext context: Context):FoodHubSession{
        return FoodHubSession(context)
    }

    @Provides
    fun provideLocationManager(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}