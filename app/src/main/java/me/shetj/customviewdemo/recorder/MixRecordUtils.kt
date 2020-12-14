package me.shetj.customviewdemo.recorder

import android.text.TextUtils
import me.shetj.base.tools.app.Utils
import me.shetj.base.tools.file.EnvironmentStorage
import me.shetj.player.PlayerListener
import me.shetj.recorder.core.*
import me.shetj.recorder.mixRecorder.MixRecorder
import me.shetj.recorder.mixRecorder.PlayBackMusic
import me.shetj.recorder.mixRecorder.mixRecorder

/**
 * 录音工具类
 */
class MixRecordUtils(
    private val callBack: SimRecordListener?
) : RecordListener, PermissionListener {
    val isRecording: Boolean
        get() {
            return if (mRecorder != null) {
                mRecorder?.isRecording!! && !mRecorder?.isPause!!
            } else {
                false
            }
        }

    fun hasRecord(): Boolean {
        return if (mRecorder != null) {
            mRecorder?.duration!! > 0 && mRecorder!!.state != RecordState.STOPPED
        } else {
            false
        }
    }

    init {
        initRecorder()
    }

    private var startTime: Long = 0 //秒 s
    private var mRecorder: BaseRecorder? = null
    var saveFile :String ? = null
        private set

    @JvmOverloads
    fun startOrPause(file: String = "") {
        if (mRecorder == null) {
            initRecorder()
        }
        when (mRecorder?.state) {
            RecordState.STOPPED -> {
                if (TextUtils.isEmpty(file)) {
                    val mRecordFile =
                        EnvironmentStorage.getPath(packagePath = "record") + "/" + System.currentTimeMillis() + ".mp3"
                    this.saveFile = mRecordFile
                } else {
                    this.saveFile = file
                }
                mRecorder?.onReset()
                mRecorder?.setOutputFile(saveFile!!, !TextUtils.isEmpty(file))
                mRecorder?.start()
            }
            RecordState.PAUSED -> {
                mRecorder?.onResume()
            }
            RecordState.RECORDING -> {
                mRecorder?.onPause()
            }
        }
    }

    @JvmOverloads
    fun startOrComplete(file: String = "") {
        if (mRecorder == null) {
            initRecorder()
        }
        when (mRecorder?.state) {
            RecordState.STOPPED -> {
                if (TextUtils.isEmpty(file)) {
                    val mRecordFile =
                        EnvironmentStorage.getPath(packagePath = "record") + "/" + System.currentTimeMillis() + ".mp3"
                    this.saveFile = mRecordFile
                } else {
                    this.saveFile = file
                }
                mRecorder?.onReset()
                mRecorder?.setOutputFile(saveFile!!, !TextUtils.isEmpty(file))
                mRecorder?.start()
            }
            RecordState.RECORDING -> {
                mRecorder?.stop()
            }
        }
    }

    /**
     * VOICE_COMMUNICATION 消除回声和噪声问题
     * MIC 麦克风- 因为有噪音问题
     */
    private fun initRecorder() {
        mRecorder = mixRecorder(
            Utils.app,
            mMaxTime = 3600 * 1000,
            recordListener = this,
            permissionListener = this,
            isDebug = true
        )
    }

    fun isPause(): Boolean {
        return mRecorder?.state == RecordState.PAUSED
    }

    fun setBackgroundPlayerListener(listener: PlayerListener) {
        mRecorder?.setBackgroundMusicListener(listener)
    }

    fun getBgPlayer(): PlayBackMusic {
        return (mRecorder!! as MixRecorder).bgPlayer
    }

    fun pause() {
        mRecorder?.onPause()
    }

    fun clear() {
        mRecorder?.onDestroy()
    }

    fun reset() {
        mRecorder?.onReset()
    }

    fun cleanPath(){
        saveFile = null
    }
    /**
     * 录音异常
     */
    private fun resolveError() {
        saveFile?.let { FileUtils.deleteFile(it) }
        if (mRecorder != null && mRecorder!!.isRecording) {
            mRecorder!!.stop()
        }
    }

    /**
     * 停止录音
     */
    fun stopFullRecord() {
        mRecorder?.stop()
    }

    override fun needPermission() {
        callBack?.needPermission()
    }

    override fun onStart() {
        callBack?.onStart()
    }

    override fun onResume() {
        callBack?.onResume()
    }

    override fun onReset() {
        callBack?.onReset()
    }

    override fun onRecording(time: Long, volume: Int) {
        callBack?.onRecording(startTime + time, volume)
    }

    override fun onPause() {
        callBack?.onPause()
    }

    override fun onRemind(mDuration: Long) {
        callBack?.onRemind(mDuration)
    }

    override fun onSuccess(file: String, time: Long) {
        callBack?.onSuccess(file, time)
    }

    override fun setMaxProgress(time: Long) {
        callBack?.setMaxProgress(time)
    }

    override fun onError(e: Exception) {
        resolveError()
        callBack?.onError(e)
    }

    override fun autoComplete(file: String, time: Long) {
        callBack?.autoComplete(file, time)
    }

    fun setVolume(volume: Float) {
        mRecorder?.setVolume(volume)
    }


}
