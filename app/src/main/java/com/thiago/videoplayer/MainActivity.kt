package com.thiago.videoplayer

import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.SimpleExoPlayer
import com.thiago.videoplayer.databinding.ActivityMainBinding
import com.thiago.videoplayer.extensions.initPlayerSession
import com.thiago.videoplayer.extensions.stopAndPrepare

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mPlayer: SimpleExoPlayer? = null
    private var mMediaSession: MediaSessionCompat? = null
    private var mWasPlaying = false

    /**
     * onCreate
     * @param savedInstanceState: Bundle?
     * initPlayerSession and initPlayerControls
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initPlayerSession(binding.playerView).apply {
            initPlayerListeners(player)
            initMediaSession(mediaSession)
        }

        initPlayerControls()
    }

    /**
     * Init Media Session and add Callbacks
     * @param mediaSession: MediaSessionCompat
     */
    private fun initMediaSession(mediaSession: MediaSessionCompat) {
        mMediaSession = mediaSession.also {
            it.setCallback(object : MediaSessionCompat.Callback() {
                override fun onStop() {
                    mPlayer?.stopAndPrepare {
                        togglePopup()
                    }
                }

                override fun onPause() {
                    mPlayer?.pause()
                }

                override fun onPlay() {
                    mPlayer?.play()
                }
            })
        }
    }

    /**
     * Init Player and add Listeners
     * @param player: SimpleExoPlayer
     */
    private fun initPlayerListeners(player: SimpleExoPlayer) {
        mPlayer = player.also {
            it.addListener(object: Listener {

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)

                    binding.apply {
                        playerView.keepScreenOn = isPlaying

                        if(isPlaying) {
                            controls.pause.visibility = View.VISIBLE
                            controls.play.visibility = View.GONE
                            togglePopup(false)
                        } else {
                            controls.pause.visibility = View.GONE
                            controls.play.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onPlaybackStateChanged(state: Int) {
                    super.onPlaybackStateChanged(state)

                    if(state == STATE_IDLE || state == STATE_ENDED) {
                        mPlayer?.stopAndPrepare {
                            togglePopup()
                        }
                    }
                }
            })
        }
    }

    /**
     * Init View Controls for Player
     */
    private fun initPlayerControls() {
        binding.controls.apply {
            play.setOnClickListener {
                mPlayer?.play()
            }

            pause.setOnClickListener {
                mPlayer?.pause()
            }

            stop.setOnClickListener {
                mPlayer?.stop()
            }
        }
    }

    /**
     * Toggle Popup Visibility
     * @param show: Boolean
     */
    private fun togglePopup(show: Boolean = true) {
        binding.popUp.visibility = if(show) View.VISIBLE else View.GONE
    }

    /**
     * onStart
     * Play if player was playing before goes to background
     * Activate Media Session
     */
    override fun onStart() {
        super.onStart()
        if(mWasPlaying) {
            mPlayer?.play()
        }
        mMediaSession?.isActive = true
    }

    /**
     * onStop
     * Pause if player is playing when goes to background
     * Deactivate Media Session
     */
    override fun onStop() {
        super.onStop()
        mPlayer?.apply {
            mWasPlaying = isPlaying
            pause()
        }
        mMediaSession?.isActive = false
    }

    /**
     * onDestroy
     * Release Player
     */
    override fun onDestroy() {
        super.onDestroy()
        mPlayer?.release()
        mPlayer = null
    }
}