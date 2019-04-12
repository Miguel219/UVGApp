package com.example.douglasdeleon.horasuvg.Model;

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.R
import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.example.douglasdeleon.horasuvg.LoginActivity


class MyFirebaseMessagingService : FirebaseMessagingService() {
   val TAG = "Service"
   override fun onMessageReceived(remoteMessage: RemoteMessage?) {
       // Handle FCM messages here.
       // If the application is in the foreground handle both data and notification messages here.
       // Also if you intend on generating your own notifications as a result of a received FCM
       // message, here is where that should be initiated.
       Log.d(TAG, "From: " + remoteMessage!!.from)
       Log.d(TAG, "Notification Message Body: " + remoteMessage.notification!!.body!!)
       val intent = Intent(this, LoginActivity::class.java).apply {
           flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
       }
       val color = -0xedcbaa
       val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
       var channelId = "channel_id"
       var bitmap=BitmapFactory.decodeResource(resources, com.example.douglasdeleon.horasuvg.R.drawable.uvggreen)
       val notificationBuilder = NotificationCompat.Builder(this,"channel_id")
           .setContentTitle(remoteMessage.notification!!.title!!)
           .setContentText(remoteMessage.notification!!.body!!)
           .setPriority(NotificationCompat.PRIORITY_DEFAULT)
           .setStyle(NotificationCompat.BigTextStyle())
           .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

           .setSmallIcon(com.example.douglasdeleon.horasuvg.R.mipmap.uvgl)
           .setLargeIcon(bitmap)
           .setAutoCancel(true)
           .setContentIntent(pendingIntent)
           .setColor(Color.GREEN)


       val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

       notificationManager.notify(1, notificationBuilder.build())


   }


}