package com.example.musicplayerappkotlin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerappkotlin.databinding.MusicViewBinding

class MusicAdapter(private val context: Context, private var musiclist: ArrayList<Music>): RecyclerView.Adapter<MusicAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.MyHolder {
       return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: MusicAdapter.MyHolder, position: Int) {
        holder.title.text = musiclist[position].title
        holder.album.text = musiclist[position].artist
        holder.duration.text = formatDuration(musiclist[position].duration)
        Glide.with(context).
                load(musiclist[position].artUri).
                apply(RequestOptions().placeholder(R.drawable.music_icon1_splash_screen).centerCrop())
            .into(holder.image)

        holder.root.setOnClickListener {
//            val intent = Intent(context, PlayerActivity::class.java)
//            intent.putExtra("index", position)
//            intent.putExtra("class", "MusicAdapter")
//
//            ContextCompat.startActivity(context, intent, null)
            when{
                MainActivity.search -> sendIntent(ref = "MusicAdapterSearch", pos = position)
                musiclist[position].id == PlayerActivity.nowPlayingId -> sendIntent(ref = "NowPlaying", pos = PlayerActivity.songPosition)
                else -> sendIntent(ref = "MusicAdapter", pos = position)
            }

        }






    }

    override fun getItemCount(): Int {
        return musiclist.size
    }

    fun updateMusicList(searchList: ArrayList<Music>){
        musiclist = ArrayList()
        musiclist.addAll(searchList)
        notifyDataSetChanged()
    }

    private fun sendIntent(ref: String, pos: Int){
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }



    class MyHolder(binding: MusicViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameMV
        val album = binding.songAlbumMV
        val image = binding.imgMV
        val duration = binding.songDuration
        val root = binding.root
    }


}