package com.hkm.consumerapp.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.hkm.consumerapp.R
import com.hkm.consumerapp.ui.MainActivity
import java.util.*

class ReminderReceiver : BroadcastReceiver() {
    companion object {
        private const val ID_REMINDER = 101
        const val EXTRA_TITTLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(EXTRA_TITTLE).toString()
        val message = intent.getStringExtra(EXTRA_MESSAGE).toString()

        showNotification(context, title, message)
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val channelId = "Channel_0"
        val channelName = "Consumer Daily Reminder"

        val activityIntent =
            PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)

        val notificationManagerCompat =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, channelId)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSmallIcon(R.drawable.ic_hub_launcher)
            .setLargeIcon(
                ContextCompat.getDrawable(context, R.drawable.ic_hub_launcher)?.toBitmap()
            )
            .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(activityIntent)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setLights(Color.RED, 3000, 3000)
            .setSound(alarmSound)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)

            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)

            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED

            builder.setChannelId(channelId)

            notificationManagerCompat.createNotificationChannel(mChannel)
        }

        val notification = builder.build()


        notificationManagerCompat.notify(ID_REMINDER, notification)

    }

    fun enableReminder(context: Context, title: String, message: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        intent.putExtra(EXTRA_TITTLE, title)
        intent.putExtra(EXTRA_MESSAGE, message)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 9)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_REMINDER, intent, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun disableReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val requestCode = ID_REMINDER
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            Intent(context, ReminderReceiver::class.java),
            0
        )
        pendingIntent.cancel()

        alarmManager.cancel(pendingIntent)
    }
}
