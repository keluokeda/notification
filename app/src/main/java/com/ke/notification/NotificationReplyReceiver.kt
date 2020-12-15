package com.ke.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput

class NotificationReplyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val bundle = RemoteInput.getResultsFromIntent(intent) ?: return
        val text = bundle.getCharSequence(MainActivity.NOTIFICATION_RESULT_KEY)
        val notificationId = intent.getIntExtra(MainActivity.NOTIFICATION_ID, 0)
        val channelId = intent.getStringExtra(MainActivity.NOTIFICATION_CHANNEL_ID) ?: ""
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(text)
            .build()
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}