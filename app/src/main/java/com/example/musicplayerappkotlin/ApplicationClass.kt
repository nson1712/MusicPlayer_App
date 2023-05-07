package com.example.musicplayerappkotlin

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build

class ApplicationClass : Application(){
    companion object{
        const val CHANNEL_ID = "channel1"
        const val PLAY = "play"
        const val NEXT = "next"
        const val PREVIOUS = "previous"
        const val EXIT = "exit"

    }

    override fun onCreate() {
        super.onCreate()
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            val notificationChannel = NotificationChannel(CHANNEL_ID, "Now Playing Song", NotificationManager.IMPORTANCE_HIGH)
//            notificationChannel.description  = "This is a important channel for showing song!!"
//            notificationChannel.enableVibration(true)
//            notificationChannel.enableLights(true)
//            notificationChannel.lightColor = Color.RED
//            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(notificationChannel)


            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(CHANNEL_ID,"Now Playing Song", NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.description = "This is an important channel for showing songs!!"
            notificationChannel.enableVibration(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


}