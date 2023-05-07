package com.example.musicplayerappkotlin

import android.annotation.TargetApi
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.Runnable

class MusicService: Service() {
    private val myBinder = MyBinder()
    var mp : MediaPlayer ?= null
    private lateinit var mediaSession : MediaSessionCompat
    private lateinit var runnable: Runnable

    override fun onBind(intent: Intent?): IBinder? {
        mediaSession = MediaSessionCompat(baseContext, "My Music")
        return myBinder
    }

    fun createMediaplayer(){
        try {
            if(PlayerActivity.musicService!!.mp==null){
                PlayerActivity.musicService!!.mp = MediaPlayer()
            }
            PlayerActivity.musicService!!.mp!!.reset()
            PlayerActivity.musicService!!.mp!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
            PlayerActivity.musicService!!.mp!!.prepare()
//            PlayerActivity.musicService!!.mp!!.start()
//            PlayerActivity.isPlaying = true
            PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
            PlayerActivity.binding.tvSeekBarStart.text = formatDuration(mp!!.currentPosition.toLong())
            PlayerActivity.binding.tvSeekBarEnd.text = formatDuration(mp!!.duration.toLong())
            PlayerActivity.binding.seekBarPA.progress = 0
            PlayerActivity.binding.seekBarPA.max = mp!!.duration
            PlayerActivity.nowPlayingId = PlayerActivity.musicListPA[PlayerActivity.songPosition].id
        }catch (e: Exception){return}
        return
    }



    inner class MyBinder : Binder(){
        fun currentService(): MusicService {
            return this@MusicService
        }
    }
    @TargetApi(26)
    fun showNotification(playPauseBtn: Int){
        //broadcast receiver
        val prevIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val playIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val nextIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val exitIntent = Intent(baseContext, NotificationReceiver::class.java).setAction(ApplicationClass.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val imgArt = getImgArt(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
        val image = if(imgArt != null){
            //giải mã ByteArray imgArt: imgArt-ByteArray, 0-chỉ số byte đầu tiên bắt đầu giải mã, imgArt.size-số lượng byte sẽ đc giải mã
            BitmapFactory.decodeByteArray(imgArt,0,imgArt.size)
        }else{
            BitmapFactory.decodeResource(resources, R.drawable.music_icon1_splash_screen)
        }


        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notifyId = 1
        val channelId = "channel1"
        val noti = NotificationCompat.Builder(baseContext, ApplicationClass.CHANNEL_ID)
            .setContentTitle(PlayerActivity.musicListPA[PlayerActivity.songPosition].title)
            .setContentText(PlayerActivity.musicListPA[PlayerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.music_icon)
            .setLargeIcon(image)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(false)
            .addAction(R.drawable.back_icon, "Previous", prevPendingIntent)
            .addAction(playPauseBtn, "Play", playPendingIntent)
            .addAction(R.drawable.next_icon, "Next", nextPendingIntent)
            .addAction(R.drawable.exit_icon, "Exit", exitPendingIntent)
            .build()

        notificationManager.notify(notifyId, noti)


//        startForegroundService(Intent(this, PlayerActivity::class.java))


//        try {
//            startForegroundService(Intent(this, PlayerActivity::class.java))
//        }catch (e : Exception){
//
//        }
    }

    fun seekBarSetup(){
        runnable = Runnable {
            PlayerActivity.binding.tvSeekBarStart.text = formatDuration(mp!!.currentPosition.toLong())
            PlayerActivity.binding.seekBarPA.progress = mp!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }

        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }
}