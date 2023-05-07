package com.example.musicplayerappkotlin

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerappkotlin.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PlayerActivity : AppCompatActivity(), ServiceConnection, MediaPlayer.OnCompletionListener {
    companion object {
        lateinit var  musicListPA: ArrayList<Music>
        var songPosition: Int=0
//        var mediaPlayer: MediaPlayer?=null
        var isPlaying: Boolean = false
        var musicService: MusicService?=null
        lateinit var binding: ActivityPlayerBinding

        var repeat: Boolean = false

        var min15: Boolean = false
        var min30: Boolean = false
        var min60: Boolean = false

        var nowPlayingId: String = ""


    }

//    private lateinit var binding: ActivityPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayerAppKotlin)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //for starting service
//        val intent = Intent(this, MusicService::class.java)
//        bindService(intent, this, BIND_AUTO_CREATE)
//        startService(intent)

        initializeLayout()


        binding.playPauseBtnPA.setOnClickListener {
            if(isPlaying){
                pauseMusic()
            }else{
                playMusic()
            }

        }

        binding.previousBtn.setOnClickListener {
            prevNextSong(increment = false)

        }

        binding.nextBtn.setOnClickListener {
            prevNextSong(increment = true)
        }

    binding.seekBarPA.setOnSeekBarChangeListener(
        object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser) musicService!!.mp!!.seekTo(progress)


            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit


        }
    )
    binding.repeatBtnPA.setOnClickListener {
        if(!repeat){
            repeat = true
            binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.green))
        }else{
            binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))
        }
    }
    //mo man hinh cai dat am thanh(audio effect)
    binding.equalizerBtnPA.setOnClickListener {
        try {
            //doi tuong intent voi muc dich hien thi man hinh cai dat am thanh
            val eqIntent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
            //gán id phiên âm thanh hiện tại của mp cho intent để màn hình cài đặt sound biêt đc sound session đang sdung
            eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, musicService!!.mp!!.audioSessionId)
            //chỉ định tên gói của app để màn hình cài đặt sound biết đc app nào đang yêu cầu show màn hình này
            eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, baseContext.packageName)
            //chỉ định loại nội dung âm thanh đc phát
            eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC)
            startActivityForResult(eqIntent, 13)
        }catch (e: Exception){
            Toast.makeText(this, "Equalizer Feature not supported!!", Toast.LENGTH_LONG).show()}
    }

    binding.timerBtnPA.setOnClickListener {
        val timer = min15 || min30 || min60
        if(!timer) {
            showBottomSheetDialog()
        }else{
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Stop Timer")
                .setMessage("Do u want to stop timer?")
                .setPositiveButton("Yes"){_,_->
                    min15 = false
                    min30 = false
                    min60 = false
                    binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink))
                }
                .setNegativeButton("No"){dialog,_->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)

        }

    }

    binding.shareBtnPA.setOnClickListener {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "audio/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(musicListPA[songPosition].path))
        //dùng createChooser để hiện dsach các ứng dụng có the share file âm thanh
        startActivity(Intent.createChooser(shareIntent, "Sharing Music File!!"))
    }



    binding.backBtnPA.setOnClickListener { finish() }




    }

    private fun prevNextSong(increment: Boolean) {
        if(increment){
            setSongPosition(increment = true)
            setLayout()
            createMediaplayer()
        }else{
            setSongPosition(increment = false)
            setLayout()
            createMediaplayer()
        }

    }



    private fun createMediaplayer(){
        try {
            if(musicService!!.mp==null){
                musicService!!.mp = MediaPlayer()
            }
            musicService!!.mp!!.reset()
            musicService!!.mp!!.setDataSource(musicListPA[songPosition].path)
            musicService!!.mp!!.prepare()
            musicService!!.mp!!.start()
            isPlaying = true
            binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            musicService!!.showNotification(R.drawable.pause_icon)


            //keo' thanh SeekBar
            binding.tvSeekBarStart.text = formatDuration(musicService!!.mp!!.currentPosition.toLong())
            binding.tvSeekBarEnd.text = formatDuration(musicService!!.mp!!.duration.toLong())
            binding.seekBarPA.progress = 0
            binding.seekBarPA.max = musicService!!.mp!!.duration
            musicService!!.mp!!.setOnCompletionListener(this)

            nowPlayingId = musicListPA[songPosition].id
        }catch (e: Exception){return}
        return
    }

    private fun setLayout(){
        Glide.with(this).
        load(musicListPA[songPosition].artUri).
        apply(RequestOptions().placeholder(R.drawable.music_icon1_splash_screen).centerCrop())
            .into(binding.songImgPA)

        binding.songNamePA.text = musicListPA[songPosition].title

        if(repeat) binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.green))
        if(min15 || min30 || min60) binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.green))
    }

    private fun initializeLayout(){
        songPosition = intent.getIntExtra("index", 0)
        when(intent.getStringExtra("class")){

            "NowPlaying" -> {
                setLayout()
                binding.tvSeekBarStart.text = formatDuration(musicService!!.mp!!.currentPosition.toLong())
                binding.tvSeekBarEnd.text = formatDuration(musicService!!.mp!!.duration.toLong())
                binding.seekBarPA.progress = musicService!!.mp!!.currentPosition
                binding.seekBarPA.max = musicService!!.mp!!.duration
                if(isPlaying) binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
                else binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
            }

            "MusicAdapterSearch" ->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.musicListSearch)
                setLayout()
            }
            "MusicAdapter"->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA  = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                setLayout()
//                createMediaplayer()

            }
            "MainActivity"->{
                val intent = Intent(this, MusicService::class.java)
                bindService(intent, this, BIND_AUTO_CREATE)
                startService(intent)
                musicListPA = ArrayList()
                musicListPA.addAll(MainActivity.MusicListMA)
                musicListPA.shuffle()
                setLayout()
//                createMediaplayer()
            }
        }
    }

    private fun playMusic(){
        binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        musicService!!.showNotification(R.drawable.pause_icon)
        isPlaying = true
        musicService!!.mp!!.start()
    }

    private fun pauseMusic(){
        binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        musicService!!.showNotification(R.drawable.play_icon)
        isPlaying = false
        musicService!!.mp!!.pause()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder = service as MusicService.MyBinder
        musicService = binder.currentService()
        createMediaplayer()
//        musicService!!.showNotification(R.drawable.pause_icon)
        musicService!!.seekBarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        musicService = null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaplayer()
        try {
            setLayout()
        }catch (e: Exception){
            return
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 13 || resultCode == RESULT_OK){
            return
        }
    }

    //show Bottom Sheet Dialog
    private fun showBottomSheetDialog(){
        //tham chiếu đến đối tượng lớp hiện tại(this) để làm ngữ cảnh cho dialog
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()

        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener {
            Toast.makeText(baseContext, "Music will stop after 15 minutes", Toast.LENGTH_LONG).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.green))
            min15 = true
            //tạo luồng mới, chờ 15p, nếu min15 vẫn = true thì thoát
            Thread{Thread.sleep((15 * 60000).toLong())
                if(min15) exitApplication()
            }.start()

            dialog.dismiss()

        }

        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener {
            Toast.makeText(baseContext, "Music will stop after 30 minutes", Toast.LENGTH_LONG).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.green))
            min30 = true
            //tạo luồng mới, chờ 30p, nếu min30 vẫn = true thì thoát
            Thread{Thread.sleep((30 * 60000).toLong())
                if(min30) exitApplication()
            }.start()
            dialog.dismiss()

        }

        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener {
            Toast.makeText(baseContext, "Music will stop after 60 minutes", Toast.LENGTH_LONG).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.green))
            min60 = true
            //tạo luồng mới, chờ 60p, nếu min60 vẫn = true thì thoát
            Thread{Thread.sleep((60 * 60000).toLong())
                if(min60) exitApplication()
            }.start()
            dialog.dismiss()

        }

    }

}