package com.example.musicplayerappkotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.PREVIOUS -> prevNextSong(increment = false, context = context!!)
            ApplicationClass.PLAY -> if(PlayerActivity.isPlaying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> prevNextSong(increment = true, context = context!!)
            ApplicationClass.EXIT -> {
                exitApplication()
            }


        }
    }

    private fun playMusic(){
        PlayerActivity.isPlaying = true
        PlayerActivity.musicService!!.mp!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)

    }

    private fun pauseMusic(){
        PlayerActivity.isPlaying = false
        PlayerActivity.musicService!!.mp!!.start()
        PlayerActivity.musicService!!.showNotification(R.drawable.play_icon)
        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)

    }

    private fun prevNextSong(increment: Boolean, context: Context?){
        setSongPosition(increment = increment)
        PlayerActivity.musicService!!.createMediaplayer()
//        PlayerActivity.musicService!!.mp!!.setDataSource(PlayerActivity.musicListPA[PlayerActivity.songPosition].path)
//        PlayerActivity.musicService!!.mp!!.prepare()
//        PlayerActivity.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
//        PlayerActivity.musicService!!.showNotification(R.drawable.pause_icon)

        if (context != null) {
            Glide.with(context)
                .load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_icon1_splash_screen).centerCrop())
                .into(PlayerActivity.binding.songImgPA)
        }
            PlayerActivity.binding.songNamePA.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title

        if (context != null) {
            Glide.with(context).load(PlayerActivity.musicListPA[PlayerActivity.songPosition].artUri).apply(RequestOptions().placeholder(R.drawable.music_icon1_splash_screen).centerCrop())
                .into(NowPlaying.binding.songImgNP)
        }
            NowPlaying.binding.songNameNP.text = PlayerActivity.musicListPA[PlayerActivity.songPosition].title
            playMusic()




    }
}