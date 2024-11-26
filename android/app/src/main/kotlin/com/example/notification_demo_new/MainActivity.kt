package com.example.notification_demo_new

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

object Constant{
    const val REQUEST_CODE = "request_code"

}

class MainActivity: FlutterActivity() {
    private val CHANNEL = "notification"
    val CHANNEL_ID = "notification_channel_id"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
                call, result ->
            if (call.method == "send_notification") {
                val title = call.argument<String>("title")?:"Empty"
                val description = call.argument<String>("description")?:"Empty"


                createNotificationChannel(title,description)
                with(NotificationManagerCompat.from(this)) {
                    if (ActivityCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)

                        shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
                        println("check permission")
                        println(areNotificationsEnabled())
                        return@with
                    }
                    // notificationId is a unique int for each notification that you must define.
                    //notify(NOTIFICATION_ID, builder.build())
                }


                startService(
                    Intent(
                        this,
                        CountdownService::class.java
                    )
                )

                val intent = Intent(this, CountdownService::class.java)
                intent.putExtra("title", title)
                intent.putExtra("description", description)
                startService(intent)

//                createNotificationChannel(title,description)
//                with(NotificationManagerCompat.from(this)) {
//                    if (ActivityCompat.checkSelfPermission(
//                            this@MainActivity,
//                            Manifest.permission.POST_NOTIFICATIONS
//                        ) != PackageManager.PERMISSION_GRANTED
//                    ) {
//                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
//
//                        shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
//                        println("check permission")
//                        println(areNotificationsEnabled())
//                        return@with
//                    }
//                    // notificationId is a unique int for each notification that you must define.
//                    //notify(NOTIFICATION_ID, builder.build())
//                }
//
//                println("this is from method channel")
//                println(title)
//                println(description)
//
//                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                // Get the layouts to use in the custom notification.
//
//                val notificationLayout = RemoteViews(packageName, R.layout.notification_small)
//                val notificationLayoutExpanded = RemoteViews(packageName, R.layout.notification_large)
//
//                 notificationLayoutExpanded.setTextViewText(R.id.notification_title_new,title)
//                 notificationLayoutExpanded.setTextViewText(R.id.notification_body_new,description)
//
//                 notificationLayoutExpanded.setTextViewText(R.id.notification_title,title)
//
//
//
////                runnable = object : Runnable {
////                    override fun run() {
////                        try {
////                            handler.postDelayed(this, 1000)
////                            val dateFormat: SimpleDateFormat = SimpleDateFormat(DATE_FORMAT)
////                            val event_date: Date = dateFormat.parse(EVENT_DATE_TIME)
////                            val current_date: Date = Date()
////                                println("asdsadsad")
////                                val diff: Long = event_date.getTime() - current_date.getTime()
////                                val Days = diff / (24 * 60 * 60 * 1000)
////                                val Hours = diff / (60 * 60 * 1000) % 24
////                                val Minutes = diff / (60 * 1000) % 60
////                                val Seconds = diff / 1000 % 60
////                                //
////
////                                /// Apply countdown
////                                notificationLayoutExpanded.setTextViewText(R.id.notification_days,String.format("%02d", Days))
////                                notificationLayoutExpanded.setTextViewText(R.id.notification_hour,String.format("%02d", Hours))
////                                notificationLayoutExpanded.setTextViewText(R.id.notification_minutes,String.format("%02d", Minutes))
////                                notificationLayoutExpanded.setTextViewText(R.id.notification_seconds,String.format("%02d", Seconds))
////
////                        } catch (e: Exception) {
////                            e.printStackTrace()
////                        }
////                    }
////                }
////                handler.postDelayed(runnable as Runnable, 0)
//
//
//
//               // Apply the layouts to the notification.
//                val customNotification = NotificationCompat.Builder(context, CHANNEL_ID)
//                    .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(title)
//                    .setContentText(description)
//                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
//                    .setCustomContentView(notificationLayout)
//                     .setCustomBigContentView(notificationLayoutExpanded)
//                    .build()
//
//                notificationManager.notify(666, customNotification)

            }



        }
    }

    private fun createNotificationChannel(title: String, descriptionn: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, title, importance).apply {
                description = descriptionn
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}