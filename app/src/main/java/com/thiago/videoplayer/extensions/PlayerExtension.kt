package com.thiago.videoplayer.extensions

import android.content.Context
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.MediaItem
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

/**
 * Get DefaultTrackSelector from Context
 * @param width: Int
 * @param height: Int
 * @return TrackSelector
 */
private fun Context.trackSelector(width: Int, height: Int): TrackSelector = DefaultTrackSelector(this).also {
    it.parameters = it.buildUponParameters().setMaxVideoSize(width, height).build()
}

/**
 * Get SimpleExoPlayer from Context
 * @param width: Int
 * @param height: Int
 * @return SimpleExoPlayer
 */
private fun Context.getPlayer(width: Int, height: Int) : SimpleExoPlayer = SimpleExoPlayer
    .Builder(this)
    .setTrackSelector(trackSelector(width, height))
    .build()

/**
 * Get MediaItem from url
 * @param url: String
 * @return MediaItem
 */
private fun getMedia(url: String): MediaItem = MediaItem.fromUri(Uri.parse(url))

/**
 * Init MediaSessionConnector from MediaSessionCompat
 * @param player: SimpleExoPlayer
 */
private fun MediaSessionCompat.initMediaSessionConnector(player: SimpleExoPlayer) = MediaSessionConnector(this).also {
    it.setPlayer(player)

    it.setEnabledPlaybackActions(
        PlaybackStateCompat.ACTION_PLAY or
        PlaybackStateCompat.ACTION_PAUSE or
        PlaybackStateCompat.ACTION_STOP)
}

/**
 * SimpleExoPlayer stopAndPrepare
 * Reset seeker, prepare and invoke custom event
 * @param event: () -> Unit
 */
fun SimpleExoPlayer.stopAndPrepare(event: () -> Unit) {
    seekTo(0)
    playWhenReady = false
    prepare()
    event.invoke()
}

/**
 * Init Player and Media Session
 * @param playerView: PlayerView
 * @param url: String
 * @param width: Int
 * @param height: Int
 * @return PlayerSession
 */
fun Context.initPlayerSession(playerView: PlayerView,
                              url: String = DEFAULT_URL,
                              width: Int = DEFAULT_WIDTH,
                              height: Int = DEFAULT_HEIGHT): PlayerSession {
    val player = getPlayer(width, height).apply {
        playerView.player = this
        setMediaItem(getMedia(url))
        playWhenReady = true
        prepare()
    }

    val mediaSession = MediaSessionCompat(this, getString(R.string.app_name)).also {
        it.initMediaSessionConnector(player)
    }

    return PlayerSession(player, mediaSession)
}

