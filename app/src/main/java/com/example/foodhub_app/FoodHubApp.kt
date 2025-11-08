package com.example.foodhub_app

import android.app.Application
import android.util.Log
import com.example.foodhub_app.notification.FoodHubNotificationManager
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FoodHubApp : Application() {

    @Inject
    lateinit var notificationManager: FoodHubNotificationManager

    override fun onCreate() {
        super.onCreate()

        // ✅ Always call this FIRST
        initializeFirebaseSafely()

        // ✅ Then set up notifications
        notificationManager.createNotificationChannel()
        notificationManager.safeInit()
    }

    private fun initializeFirebaseSafely() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("FoodHubApp", "Firebase initialized manually.")
            } else {
                Log.d("FoodHubApp", "Firebase already initialized.")
            }
        } catch (e: Exception) {
            Log.e("FoodHubApp", "Firebase initialization failed", e)
        }
    }
}
