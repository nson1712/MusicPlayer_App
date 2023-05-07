package com.example.musicplayerappkotlin

import android.media.MediaMetadataRetriever
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class Music(
    val id: String,
    val title: String,
    val album: String,
    val artist: String,
    val duration: Long=0,
    val path: String,
    val artUri: String
)

fun formatDuration(duration: Long): String{
    val min = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val sec = TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
            min*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES)

    return String.format("%02d:%02d", min, sec)
}

fun getImgArt(path:String): ByteArray? {
    //dùng MediaMetadataRetriever để trích xuất thông tin về phg tiện đa phg tiện(tiêu đề, ảnh nhúng, thời lg, nghệ sĩ,..)
    val retriever = MediaMetadataRetriever()
    //thiết lập nguồn dữ liệu(path)
    retriever.setDataSource(path)
    //trả về ảnh nhúng, truy xuất ảnh nhúng bằng thuộc tính embeddedPicture
    return retriever.embeddedPicture
}

fun setSongPosition(increment: Boolean){
    if(!PlayerActivity.repeat){
        if(increment){
            if(PlayerActivity.musicListPA.size - 1 == PlayerActivity.songPosition){
                PlayerActivity.songPosition = 0
            }else{
                ++PlayerActivity.songPosition
            }
        }else{
            if(0 == PlayerActivity.songPosition){
                PlayerActivity.songPosition = PlayerActivity.musicListPA.size - 1
            }else{
                --PlayerActivity.songPosition
            }
        }

    }
}

fun exitApplication(){
    if(PlayerActivity.musicService != null){
        PlayerActivity.musicService!!.stopForeground(true)
        PlayerActivity.musicService!!.mp!!.release()
        PlayerActivity.musicService = null
    }
    exitProcess(1)
}

