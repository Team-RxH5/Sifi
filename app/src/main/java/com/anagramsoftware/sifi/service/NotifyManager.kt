package com.anagramsoftware.sifi.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.anagramsoftware.sifi.R
import com.anagramsoftware.sifi.ui.MainActivity

class NotifyManager(private val service: SifiService) {

    fun showNotification(what: Int) {
        val intent = Intent(service, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        // Build the notification
        val builder = NotificationCompat.Builder(service, CHANNEL_ID)
        if (what == TYPE_PROVIDE) {
            builder.setSmallIcon(R.drawable.ic_notification_provide)
            builder.setContentTitle("Providing hotspot")
            builder.setContentText("Provided: , Etd. Income:")
            intent.action = MainActivity.ACTION_PROVIDE
        } else if (what == TYPE_USE) {
            builder.setSmallIcon(R.drawable.ic_notification_use)
            builder.setContentTitle("Using Sifi hotspot")
            builder.setContentText("Sent: , Received: ")
            intent.action = MainActivity.ACTION_USE
        }
        builder.priority = NotificationCompat.PRIORITY_HIGH

        val pendingIntent = PendingIntent.getActivity(service, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        builder.setContentIntent(pendingIntent)

        val notification = builder.build()
        notification.flags = Notification.FLAG_NO_CLEAR

        NotificationManagerCompat.from(service).notify(NOTIFY_ID, notification)

        service.startForeground(NOTIFY_ID, notification)
    }

    fun removeNotification() {
        service.stopForeground(true)
        NotificationManagerCompat.from(service).cancel(NOTIFY_ID)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val name = "Sifi Notification"
        val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH)
        val manager = service.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        private const val NOTIFY_ID = 1994
        private const val CHANNEL_ID = "1998"

        val TYPE_USE = 0
        val TYPE_PROVIDE = 1
    }

}