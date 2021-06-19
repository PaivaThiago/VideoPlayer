package com.thiago.videoplayer.models

import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.SimpleExoPlayer

/**
 * Player Session Data Class
 *
 * @param player: SimpleExoPlayer
 * @param mediaSession: MediaSessionCompat
 */
data class PlayerSession(val player: SimpleExoPlayer, val mediaSession: MediaSessionCompat)