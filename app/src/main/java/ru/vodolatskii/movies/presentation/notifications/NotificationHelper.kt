package com.example.myapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.vodolatskii.movies.R
import ru.vodolatskii.movies.common.NotificationsReceiver
import ru.vodolatskii.movies.domain.models.Movie
import ru.vodolatskii.movies.presentation.MainActivity
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "default_channel"
        const val CHANNEL_NAME = "Основные уведомления"
        const val CHANNEL_DESCRIPTION = "Канал для основных уведомлений приложения"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                lightColor = android.graphics.Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            }

            notificationManager.createNotificationChannel(channel)
        }
    }


    /**
     * Кастомное уведомление с расширенным layout, картинкой 100x100 и кнопкой действия
     */
    @SuppressLint("RemoteViewLayout")
    fun showCustomExpandedNotification(
        movie: Movie,
        button1Text: String,
        button2Text: String,
        notificationId: Int = 10
    ) {

        val intent = Intent(context, MainActivity::class.java).apply {
            setPackage(context.packageName)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("movie", movie)

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val customNotificationView =
            RemoteViews(context.packageName, R.layout.custom_notification_layout)
        val customExpandedView =
            RemoteViews(context.packageName, R.layout.custom_notification_layout_exp)

        customNotificationView.setTextViewText(R.id.notification_title, movie.title)
        customNotificationView.setTextViewText(R.id.notification_message, movie.description)

        customExpandedView.setTextViewText(R.id.notification_title, movie.title)
        customExpandedView.setTextViewText(R.id.notification_message, movie.description)
        customExpandedView.setTextViewText(R.id.notification_button1, button1Text)
        customExpandedView.setTextViewText(R.id.notification_button2, button2Text)

        val buttonIntent1 = Intent(context, NotificationsReceiver::class.java).apply {
            action = "FIND"
            putExtra("query", movie.title)
            putExtra("notification_id", notificationId)
        }
        val buttonPendingIntent1 = PendingIntent.getBroadcast(
            context,
            notificationId * 2,
            buttonIntent1,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val buttonIntent2 = Intent(context, NotificationsReceiver::class.java).apply {
            action = "CANCEL"
            putExtra("notification_id", notificationId)
        }

        val buttonPendingIntent2 = PendingIntent.getBroadcast(
            context,
            notificationId * 2 + 1,
            buttonIntent2,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        customExpandedView.setOnClickPendingIntent(R.id.notification_button1, buttonPendingIntent1)
        customExpandedView.setOnClickPendingIntent(R.id.notification_button2, buttonPendingIntent2)

        CoroutineScope(Dispatchers.IO).launch {
            val bitmap = getBitmapFromUrlManual(movie.posterUrl) ?: getBitmapFromResource(
                context,
                R.drawable.outline_cancel_24
            )

            if (bitmap != null) {
                val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true)
                customNotificationView.setImageViewBitmap(R.id.notification_image, resizedBitmap)
                customExpandedView.setImageViewBitmap(R.id.notification_image, resizedBitmap)
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.tv_set_icon)
                .setCustomContentView(customNotificationView) // Обычное состояние
                .setCustomBigContentView(customExpandedView) // Расширенное состояние с кнопкой
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
            notificationManager.notify(notificationId, notification)
        }
    }

    fun showNotificationWithActions(title: String, message: String, notificationId: Int = 3) {
        val acceptIntent = Intent(context, NotificationsReceiver::class.java).apply {
            action = "ACCEPT_ACTION"
        }
        val acceptPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            acceptIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val declineIntent = Intent(context, NotificationsReceiver::class.java).apply {
            action = "DECLINE_ACTION"
        }
        val declinePendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            declineIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.outline_cancel_24)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(R.drawable.circle_fab_twin, "Принять", acceptPendingIntent)
            .addAction(R.drawable.circle_fab_twin, "Отклонить", declinePendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }


    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }


    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }

    fun areNotificationsEnabled(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }

    fun getActiveNotifications(): Array<android.service.notification.StatusBarNotification>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.activeNotifications
        } else {
            null
        }
    }

    private suspend fun getBitmapFromUrlManual(imageUrl: String): Bitmap? {
        return withContext(context = Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                BitmapFactory
                    .decodeStream(input)
                    ?.let {
                        Bitmap.createScaledBitmap(it, 100, 100, true)
                    }
            } catch (e: IOException) {
                e.printStackTrace()
                getBitmapFromResource(context, R.drawable.outline_cancel_24)
            }
        }
    }

    private fun getBitmapFromResource(context: Context, resourceId: Int): Bitmap? {
        return BitmapFactory.decodeResource(context.resources, resourceId)
    }
}
