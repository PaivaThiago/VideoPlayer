package com.thiago.videoplayer

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.SimpleExoPlayer
import com.thiago.videoplayer.databinding.ActivityMainBinding
import com.thiago.videoplayer.extensions.initPlayerSession

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mPlayer: SimpleExoPlayer? = null
    private var mMediaSession: MediaSessionCompat? = null
    private var mWasPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayerSession(binding.playerView).apply {
           mPlayer = player
           mMediaSession = mediaSession
        }
    }

    override fun onStart() {
        super.onStart()
        if(mWasPlaying) {
            mPlayer?.play()
        }
        mMediaSession?.isActive = true
    }

    override fun onStop() {
        super.onStop()
        mPlayer?.apply {
            mWasPlaying = isPlaying
            pause()
        }
        mMediaSession?.isActive = false
    }

    override fun onDestroy() {
        super.onDestroy()
        mPlayer?.release()
        mPlayer = null
    }
}