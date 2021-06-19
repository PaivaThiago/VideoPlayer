package com.thiago.videoplayer.extensions

import android.content.Context
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.thiago.videoplayer.R
import com.thiago.videoplayer.models.PlayerSession


private const val DEFAULT_URL = "https://multiplatform-f.akamaihd.net/i/multi/will/bunny/big_buck_" +
        "bunny_,640x360_400,640x360_700,640x360_1000,950x540_1500,.f4v.csmil/master.m3u8"

private const val DEFAULT_WIDTH : Int = 1920

private const val DEFAULT_HEIGHT : Int = 1080

private fun getUri(url: String): Uri = Uri.parse(url)

private fun Context.trackSelector(width: Int, height: Int): TrackSelector = DefaultTrackSelector(this).also {
    it.parameters = it.buildUponParameters().setMaxVideoSize(width, height).build()
}

private fun playerListener(playerView: PlayerView) = object : Player.Listener {
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        playerView.keepScreenOn = isPlaying
    }
}

private fun Context.getPlayer(width: Int, height: Int) : SimpleExoPlayer = SimpleExoPlayer
    .Builder(this)
    .setTrackSelector(trackSelector(width, height))
    .build()

private fun getMedia(url: String): MediaItem = MediaItem.fromUri(getUri(url))

private fun Context.getMediaSession(): MediaSessionCompat = MediaSessionCompat(this, getString(R.string.app_name))

private fun MediaSessionCompat.initMediaSessionConnector(player: SimpleExoPlayer) = MediaSessionConnector(this).also {
    it.setPlayer(player)
    it.setEnabledPlaybackActions(
        PlaybackStateCompat.ACTION_PLAY or
        PlaybackStateCompat.ACTION_PAUSE)
}

fun Context.initPlayerSession(playerView: PlayerView, url: String = DEFAULT_URL, width: Int = DEFAULT_WIDTH, height: Int = DEFAULT_HEIGHT) : PlayerSession {
    val player = getPlayer(width, height).apply {
        playerView.player = this
        addListener(playerListener(playerView))
        setMediaItem(getMedia(url))
        prepare()
        playWhenReady = true
    }

    val mediaSession = getMediaSession().also {
        it.initMediaSessionConnector(player)
    }

    return PlayerSession(player, mediaSession)
}

