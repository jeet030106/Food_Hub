package com.example.foodhub_app.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.foodhub_app.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FoodHubMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationManager: FoodHubNotificationManager

    override fun onCreate() {
        super.onCreate()
        Log.d("FoodHubMessagingService", "Service created ✅") // 🔹 Add this line
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FoodHubMessagingService", "New token: $token") // 🔹 Add this too
        notificationManager.updateToken(token)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FoodHubMessagingService", "Message received ✅") // 🔹 Log this too
        Log.d("FoodHubMessagingService", "Message data: ${message.data}")
        Log.d("FoodHubMessagingService", "Message notification: ${message.notification}")

        val intent = Intent(this, MainActivity::class.java)
        val title = message.notification?.title
        val messageText = message.notification?.body
        val data = message.data
        val type = data["type"] ?: "general"

        if (type == "order") {
            val orderId = data[ORDER_ID]
            intent.putExtra(ORDER_ID, orderId)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationChannelType = when (type) {
            "order" -> FoodHubNotificationManager.NotificationChannelType.ORDER
            "general" -> FoodHubNotificationManager.NotificationChannelType.PROMOTION
            else -> FoodHubNotificationManager.NotificationChannelType.ACCOUNT
        }

        notificationManager.showNotification(
            title,
            messageText,
            pendingIntent,
            notificationChannelType,
            1
        )
    }

    companion object {
        const val ORDER_ID = "order_id"
    }
}
