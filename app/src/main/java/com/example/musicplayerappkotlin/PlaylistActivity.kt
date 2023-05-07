package com.example.musicplayerappkotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PlaylistActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_MusicPlayerAppKotlin)
        setContentView(R.layout.activity_playlist)
    }
}