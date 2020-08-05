package me.shetj.customviewdemo.mediaplayer

import android.content.Context
import android.net.Uri
import android.view.Surface
import android.view.SurfaceHolder
import com.google.android.exoplayer2.C
import tv.danmaku.ijk.media.player.AbstractMediaPlayer
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import tv.danmaku.ijk.media.player.MediaInfo
import tv.danmaku.ijk.media.player.misc.IMediaDataSource
import tv.danmaku.ijk.media.player.misc.ITrackInfo
import java.io.FileDescriptor

object PlayerManager : IMediaPlayer {

    private var MediaPlayer: Class<out AbstractMediaPlayer?>? = null

    fun changePlayer(playManager: Class<out AbstractMediaPlayer?>?) {
        MediaPlayer = playManager
    }

    private var playerManager: MediaPlayerProxy? = null

    fun initPlayManager(): PlayerManager {
        if (MediaPlayer == null){
            MediaPlayer = IjkMediaPlayer::class.java
        }
        playerManager = MediaPlayerProxy(MediaPlayer!!)
        return this
    }

    override fun setOnPreparedListener(var1: IMediaPlayer.OnPreparedListener?) {
        playerManager?.getMediaPlayer()?.setOnPreparedListener(var1)
    }

    override fun setDisplay(p0: SurfaceHolder?) {
        //默认不处理
    }

    override fun setOnCompletionListener(var1: IMediaPlayer.OnCompletionListener?) {
        playerManager?.getMediaPlayer()?.setOnCompletionListener(var1)
    }

    override fun setOnBufferingUpdateListener(var1: IMediaPlayer.OnBufferingUpdateListener?) {
        playerManager?.getMediaPlayer()?.setOnBufferingUpdateListener(var1)
    }

    override fun isPlayable(): Boolean {
        return playerManager?.getMediaPlayer()?.isPlayable ?: true
    }

    override fun setOnSeekCompleteListener(var1: IMediaPlayer.OnSeekCompleteListener?) {
        playerManager?.getMediaPlayer()?.setOnSeekCompleteListener(var1)
    }

    override fun setOnVideoSizeChangedListener(var1: IMediaPlayer.OnVideoSizeChangedListener?) {
        playerManager?.getMediaPlayer()?.setOnVideoSizeChangedListener(var1)
    }

    override fun setOnErrorListener(var1: IMediaPlayer.OnErrorListener?) {
        playerManager?.getMediaPlayer()?.setOnErrorListener(var1)
    }

    override fun prepareAsync() {
        playerManager?.getMediaPlayer()?.prepareAsync()
    }

    override fun setAudioStreamType(p0: Int) {
        playerManager?.getMediaPlayer()?.setAudioStreamType(p0)
    }

    override fun setOnInfoListener(var1: IMediaPlayer.OnInfoListener?) {
        playerManager?.getMediaPlayer()?.setOnInfoListener(var1)
    }

    override fun setOnTimedTextListener(var1: IMediaPlayer.OnTimedTextListener?) {
        playerManager?.getMediaPlayer()?.setOnTimedTextListener(var1)
    }

    override fun setWakeMode(p0: Context?, p1: Int) {
        playerManager?.getMediaPlayer()?.setWakeMode(p0, p1)
    }


    override fun isLooping(): Boolean {
        return playerManager?.getMediaPlayer()?.isLooping ?: false
    }

    override fun getDuration(): Long {
        return playerManager?.duration ?: 0L
    }

    override fun getDataSource(): String {
        return playerManager?.getMediaPlayer()?.dataSource ?: ""
    }

    override fun seekTo(time: Long) {
        playerManager?.seekTo(time)
    }

    override fun setLogEnabled(p0: Boolean) {
        playerManager?.getMediaPlayer()?.setLogEnabled(p0)
    }

    override fun getTrackInfo(): Array<out ITrackInfo>? {
        return playerManager?.getMediaPlayer()?.trackInfo
    }

    override fun reset() {
        playerManager?.getMediaPlayer()?.reset()
    }

    override fun getCurrentPosition(): Long {
        return playerManager?.currentPosition ?: 0L
    }

    override fun start() {
        playerManager?.start()
    }

    override fun setVolume(p0: Float, p1: Float) {
        playerManager?.getMediaPlayer()?.setVolume(p0, p1)
    }

    override fun getVideoSarDen(): Int {
        return playerManager?.videoSarDen ?: 0
    }

    override fun setDataSource(p0: Context?, p1: Uri?) {
        playerManager?.getMediaPlayer()?.setDataSource(p0, p1)
    }

    override fun setDataSource(p0: Context?, p1: Uri?, p2: MutableMap<String, String>?) {
        playerManager?.getMediaPlayer()?.setDataSource(p0, p1, p2)
    }

    override fun setDataSource(p0: FileDescriptor?) {
        playerManager?.getMediaPlayer()?.setDataSource(p0)
    }

    override fun setDataSource(p0: String?) {
        playerManager?.getMediaPlayer()?.dataSource = p0
    }

    override fun setDataSource(p0: IMediaDataSource?) {
        playerManager?.getMediaPlayer()?.setDataSource(p0)
    }

    override fun setKeepInBackground(p0: Boolean) {
        playerManager?.getMediaPlayer()?.setKeepInBackground(p0)
    }

    override fun getMediaInfo(): MediaInfo? {
        return playerManager?.getMediaPlayer()?.mediaInfo
    }

    override fun isPlaying(): Boolean {
        return playerManager?.isPlaying ?: false
    }

    override fun pause() {
        playerManager?.pause()
    }

    override fun setScreenOnWhilePlaying(p0: Boolean) {
        playerManager?.getMediaPlayer()?.setScreenOnWhilePlaying(p0)
    }

    override fun getAudioSessionId(): Int {
        return playerManager?.getMediaPlayer()?.audioSessionId ?: C.AUDIO_SESSION_ID_UNSET;
    }

    override fun getVideoWidth(): Int {
        return  0
    }

    override fun setLooping(p0: Boolean) {
        playerManager?.getMediaPlayer()?.isLooping = p0
    }


    override fun getVideoHeight(): Int {
        return 0//默认不处理
    }

    override fun getVideoSarNum(): Int {
        return 0//默认不处理
    }

    override fun setSurface(p0: Surface?) {
        //默认不处理
    }

    override fun stop() {
        playerManager?.stop()
    }

    fun setSpeed(speed: Float):Boolean {
       return playerManager?.setSpeed(speed, false)?:false
    }

    override fun release() {
        playerManager?.release()
    }
}