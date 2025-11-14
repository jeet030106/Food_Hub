package com.example.foodhub_app.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class FoodHubSession(context: Context) {

    val sharedPres: SharedPreferences =context.getSharedPreferences("FoodHub", Context.MODE_PRIVATE)
    fun storeToken(token:String){
        sharedPres.edit().putString("token",token).apply()
    }
    fun getToken():String?{
        sharedPres.getString("token",null)?.let{
            return it
        }
        return null
    }

    fun storeRestaurantId(restaurantId:String){
        sharedPres.edit().putString("restaurantId",restaurantId).apply()
    }
    fun getRestaurantId():String?{
        sharedPres.getString("restaurantId",null)?.let{
            return it
        }
        return null
    }
}