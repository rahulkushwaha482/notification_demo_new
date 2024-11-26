package com.example.notification_demo_new

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class CountdownService : Service() {
    private lateinit var notificationLayout: RemoteViews
    private lateinit var notificationManager: NotificationManagerCompat
    private val handler = Handler()
    private var endTime: Long = 0 // Timestamp in milliseconds when the offer ends
    private lateinit var mBuilder: NotificationCompat.Builder
    private lateinit var title:String
    private lateinit var description:String


    override fun onCreate() {
        super.onCreate()
        notificationManager = NotificationManagerCompat.from(this@CountdownService)
        createNotificationChannel()

        // Set the end time for the countdown (e.g., 10 minutes from now)
        endTime = System.currentTimeMillis() + 10 * 60 * 1000

        notificationLayout = RemoteViews(packageName, R.layout.notification_large)
        mBuilder = createNotificationBuilder()

        startForeground(NOTIFICATION_ID, mBuilder.build())

        handler.post(updateTimerRunnable)

        val dismissIntent = Intent(applicationContext, CountdownService::class.java)
        dismissIntent.putExtra(REQUEST_CODE, 202)
        val dismiss = PendingIntent.getService(applicationContext, 202, dismissIntent, PendingIntent.FLAG_IMMUTABLE)
        notificationLayout.setOnClickPendingIntent(R.id.btnCancel, dismiss)


    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        println("REQUEST CODE :: ${intent.getIntExtra(Constant.REQUEST_CODE, -1)}")
        if (intent.getIntExtra(Constant.REQUEST_CODE, -1) == 202) {
            stopSelf()
        }

         title = intent.getStringExtra("title").toString() // Retrieve the data
         description = intent.getStringExtra("description").toString() // Retrieve the data


        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateTimerRunnable)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private val updateTimerRunnable: Runnable = object : Runnable {
        override fun run() {
            val currentTime = System.currentTimeMillis()
            val remainingTime = endTime - currentTime

            if (remainingTime > 0) {
                val hours = remainingTime / (1000 * 60 * 60)
                val minutes = (remainingTime % (1000 * 60 * 60)) / (1000 * 60)
                val seconds = (remainingTime % (1000 * 60)) / 1000

                notificationLayout.setTextViewText(R.id.hours, String.format("%02d", hours))
                notificationLayout.setTextViewText(R.id.minutes, String.format("%02d", minutes))
                notificationLayout.setTextViewText(R.id.seconds, String.format("%02d", seconds))
                notificationLayout.setTextViewText(R.id.title, title)
                notificationLayout.setTextViewText(R.id.message,description)

                // Update the notification
                notificationManager.notify(NOTIFICATION_ID, mBuilder.build())

                handler.postDelayed(this, 1000)
            } else {
                stopSelf()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Promotion Channel"
            val description = "Channel for promotional notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createNotificationBuilder(): NotificationCompat.Builder {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val deleteIntent = Intent(this, CountdownService::class.java).apply {
            action = "DELETE_NOTIFICATION"
        }
        val deletePendingIntent = PendingIntent.getService(
            this, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )




        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setDeleteIntent(deletePendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
    }

    companion object {
        private const val CHANNEL_ID = "promotion_channel"
        const val NOTIFICATION_ID = 1
        const val REQUEST_CODE = "request_code"
        const val ACTION_CANCEL = "CANCEL"
    }
}
