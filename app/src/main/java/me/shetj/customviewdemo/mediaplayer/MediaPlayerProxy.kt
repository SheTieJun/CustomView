package me.shetj.customviewdemo.mediaplayer

import android.media.PlaybackParams
import android.os.Build
import com.shuyu.gsyvideoplayer.utils.Debuger
import me.shetj.base.tools.app.Utils
import timber.log.Timber
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer
import tv.danmaku.ijk.media.player.AbstractMediaPlayer
import tv.danmaku.ijk.media.player.AndroidMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 * 代理，加通过不同class生成
 */
class MediaPlayerProxy(private val playerClazz: Class<out AbstractMediaPlayer>) {

    private var abstractMediaPlayer: AbstractMediaPlayer? = null


    fun getMediaPlayer(): AbstractMediaPlayer? {
        if (abstractMediaPlayer == null) {
            initMediaPlayer()
        }
        return abstractMediaPlayer
    }

    private fun initMediaPlayer() {
        abstractMediaPlayer = getMediaPlay()
        Timber.tag("MediaPlayerProxy").i(abstractMediaPlayer?.javaClass?.simpleName)
    }

    private fun getMediaPlay(): AbstractMediaPlayer? {
        return when (playerClazz) {
            is IjkExo2MediaPlayer -> {
                IjkExo2MediaPlayer(Utils.app)
            }
            is AndroidMediaPlayer -> {
                AndroidMediaPlayer()
            }
            else -> {
                IjkMediaPlayer()
            }
        }
    }

    fun setSpeed(speed: Float, soundTouch: Boolean):Boolean {
        if (abstractMediaPlayer == null) return false
        when (abstractMediaPlayer) {
            is IjkMediaPlayer -> {
                (abstractMediaPlayer as IjkMediaPlayer).setSpeed(speed)
                (abstractMediaPlayer as IjkMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", if (soundTouch) 1 else 0.toLong())
            }
            is IjkExo2MediaPlayer -> {
                (abstractMediaPlayer as IjkExo2MediaPlayer).setSpeed(speed,1f)
            }
            is AndroidMediaPlayer ->{
                if ((abstractMediaPlayer as AndroidMediaPlayer).internalMediaPlayer != null && (abstractMediaPlayer as AndroidMediaPlayer).isPlayable) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val playbackParams = PlaybackParams()
                            playbackParams.speed = speed
                            (abstractMediaPlayer as AndroidMediaPlayer).internalMediaPlayer.playbackParams = playbackParams
                        } else {
                            Debuger.printfError(" not support setSpeed")
                            return false
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return false
                    }
                }
            }
        }
        return true
    }

    fun setNeedMute(needMute: Boolean) {
        if (abstractMediaPlayer != null) {
            if (needMute) {
                abstractMediaPlayer!!.setVolume(0f, 0f)
            } else {
                abstractMediaPlayer!!.setVolume(1f, 1f)
            }
        }
    }

    fun release() {
        abstractMediaPlayer?.release()
        abstractMediaPlayer = null
    }

    fun setSpeedPlaying(speed: Float, soundTouch: Boolean) {
        if (abstractMediaPlayer != null) {
            when (abstractMediaPlayer) {
                is IjkMediaPlayer -> {
                    (abstractMediaPlayer as IjkMediaPlayer).setSpeed(speed)
                    (abstractMediaPlayer as IjkMediaPlayer).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", if (soundTouch) 1 else 0.toLong())
                }
                is IjkExo2MediaPlayer -> {
                    (abstractMediaPlayer as IjkExo2MediaPlayer).setSpeed(speed,1f)
                }
                is AndroidMediaPlayer -> {
                    if ((abstractMediaPlayer as AndroidMediaPlayer).internalMediaPlayer != null && (abstractMediaPlayer as AndroidMediaPlayer).isPlayable) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                val playbackParams = PlaybackParams()
                                playbackParams.speed = speed
                                (abstractMediaPlayer as AndroidMediaPlayer).internalMediaPlayer.playbackParams = playbackParams
                            } else {
                                Debuger.printfError(" not support setSpeed")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    fun start() {
        abstractMediaPlayer?.start()
    }

    fun stop() {
        abstractMediaPlayer?.stop()
    }

    fun pause() {
        abstractMediaPlayer?.pause()
    }

    val isPlaying: Boolean
        get() = abstractMediaPlayer?.isPlaying ?: false

    fun seekTo(time: Long) {
        abstractMediaPlayer?.seekTo(time)
    }

    val currentPosition: Long
        get() = abstractMediaPlayer?.currentPosition ?: 0

    val duration: Long
        get() = abstractMediaPlayer?.duration ?: 0

    val videoSarDen: Int
        get() = abstractMediaPlayer?.videoSarDen ?: 1
}