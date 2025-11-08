package com.example.foodhub_app.notification

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.foodhub_app.data.FoodApi
import com.example.foodhub_app.data.model.FCMRequest
import com.example.foodhub_app.data.remote.ApiResponse
import com.example.foodhub_app.data.remote.safeApiCall
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodHubNotificationManager @Inject constructor(
    private val foodApi: FoodApi,
    @ApplicationContext private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)
    private val job =
        CoroutineScope(Dispatchers.IO + SupervisorJob())

    enum class NotificationChannelType(
        val id: String,
        val channelName: String,
        val channelDescription: String,
        val importance: Int
    ) {
        ORDER(
            "1",
            "Order",
            "Order notifications",
            NotificationManager.IMPORTANCE_HIGH
        ),
        PROMOTION(
            "2",
            "Promotion",
            "Promotional notifications",
            NotificationManager.IMPORTANCE_DEFAULT
        ),
        ACCOUNT("3", "Account", "Account-related notifications", NotificationManager.IMPORTANCE_LOW)
    }

    fun createNotificationChannel() {
        NotificationChannelType.entries.forEach {
            val channel =
                NotificationChannelCompat.Builder(it.id, it.importance).setName(it.channelName)
                    .setDescription(it.channelDescription)
                    .build()
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun safeInit() {
        try {
            // ✅ Ensure Firebase is initialized before calling FirebaseMessaging
            val apps = FirebaseApp.getApps(context)
            if (apps.isEmpty()) {
                Log.w("FCM", "Firebase not initialized yet — initializing manually.")
                FirebaseApp.initializeApp(context)
            }

            // ✅ Now safely get token
            FirebaseMessaging.getInstance().token
                .addOnSuccessListener { token ->
                    Log.d("FCM", "Token retrieved successfully: $token")
                    updateToken(token)
                }
                .addOnFailureListener { e ->
                    Log.e("FCM", "Failed to retrieve FCM token", e)
                }

        } catch (e: Exception) {
            Log.e("FCM", "Exception in safeInit()", e)
        }
    }


    fun updateToken(token: String) {
        job.launch {
            try {
                Log.d("FCM", "Trying to update token: $token")
                val res =
                    safeApiCall { foodApi.updateToken(FCMRequest(token)) }
                if (res is ApiResponse.Success) {
                    Log.d("FCM", "Success: ${res.data.message}")
                } else {
                    Log.d("FCM", "Error: $res")
                }
            } catch (e: Exception) {
                Log.e("FCM", "Exception updating token", e)
            }
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotification(
        title: String?,
        message: String?,
        intent: PendingIntent,
        notificationChannelType: NotificationChannelType,
        notificationId: Int
    ) {
        val notification =
            NotificationCompat.Builder(context, notificationChannelType.id).setContentTitle(title)
                .setContentText(message).setContentIntent(intent)
                .setSmallIcon(android.R.drawable.ic_dialog_info).setAutoCancel(true)
                .build()
        notificationManager.notify (notificationId, notification)
    }
}