package me.shetj.customviewdemo.recorder

import android.Manifest
import android.transition.TransitionManager
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import me.shetj.base.ktx.hasPermission
import me.shetj.base.ktx.logi
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.databinding.RecordLayoutPopupBinding
import me.shetj.recorder.core.SimRecordListener
import java.util.concurrent.TimeUnit


typealias Success = (file: String) -> Unit


class RecorderPopup(private val mContext: AppCompatActivity, private val onSuccess: Success) :
    BasePopupWindow<RecordLayoutPopupBinding>(mContext) {

    private val maxTime = (2 * 60 * 1000).toLong()

    private var isComplete = false


    private val playerListener: SimPlayerListener = object : SimPlayerListener() {
        override fun onCompletion() {
            super.onCompletion()
            mViewBinding.tvRecordTime.text = formatSeconds(0)
            mViewBinding.tvState.text = "试听"
            mViewBinding.ivRecordState.setImageResource(R.drawable.ic_record_audition)
        }

        override fun onPause() {
            super.onPause()
            mViewBinding.tvState.text = "已暂停试听"
            mViewBinding.ivRecordState.setImageResource(R.drawable.ic_record_audition)
        }

        override fun onStart(duration: Int) {
            super.onStart(duration)
            mViewBinding.tvState.text = "播放中"
            mViewBinding.tvState.isVisible = true
            mViewBinding.tvRecordDuration.text = "/" + formatSeconds((duration / 1000).toLong())
            mViewBinding.ivRecordState.setImageResource(R.drawable.ic_record_pause)
        }

        override fun onProgress(current: Int, duration: Int) {
            super.onProgress(current, duration)
            mViewBinding.tvRecordTime.text = formatSeconds((current / 1000).toLong())
        }

        override fun onResume() {
            super.onResume()
            mViewBinding.tvState.text = "播放中"
            mViewBinding.ivRecordState.setImageResource(R.drawable.ic_record_pause)
        }
    }

    private val listener: SimRecordListener = object : SimRecordListener() {

        override fun onStart() {
            TransitionManager.beginDelayedTransition(mViewBinding.root)
            mViewBinding.tvReRecord.isVisible = true
            mViewBinding.tvSaveRecord.isVisible = true
            mViewBinding.llTime.isVisible = true
            mViewBinding.tvTips.isVisible = false
            mViewBinding.ivRecordState.setImageResource(R.drawable.ic_record_pause)
        }

        override fun autoComplete(file: String, time: Long) {
            super.autoComplete(file, time)
            onShowSuccessView()
        }

        override fun onRecording(time: Long, volume: Int) {
            super.onRecording(time, volume)
            mViewBinding.tvRecordTime.text = formatSeconds(time / 1000)
        }

        override fun onMaxChange(time: Long) {
            super.onMaxChange(time)
            mViewBinding.tvRecordDuration.text = "/" + formatSeconds(time)
        }

        override fun onSuccess(file: String, time: Long) {
            super.onSuccess(file, time)
            onShowSuccessView()
        }

        override fun onReset() {
            isComplete = false
            mViewBinding.tvTips.isVisible = true
            mViewBinding.tvTips.text = "点击下方按钮开始录音"
            mViewBinding.llTime.isVisible = false
            mViewBinding.tvReRecord.isVisible = false
            mViewBinding.tvSaveRecord.isVisible = false
            mViewBinding.tvState.text = ""
            mViewBinding.tvRecordTime.text = formatSeconds(0)
            mViewBinding.tvRecordDuration.text = "/" + formatSeconds(maxTime)
            mViewBinding.ivRecordState.setImageResource(R.drawable.ic_record_start)
        }

        override fun needPermission() {
            super.needPermission()
            mContext.hasPermission(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, isRequest = true
            )
        }

        override fun onRemind(duration: Long) {
            super.onRemind(duration)
            "录音中，60秒后自动保存".logi()
            mViewBinding.tvTips.isVisible = true
            mViewBinding.tvTips.text = "录音中，60秒后自动保存"
        }

        override fun onError(e: Exception) {
            super.onError(e)
            onReset()
        }
    }

    private val recordUtils by lazy { MixRecordUtils(maxTime,listener)}

    private val player: AudioPlayer by lazy { AudioPlayer() }

    override fun RecordLayoutPopupBinding.initUI() {
        ivRecordState.setOnClickListener {
            if (!isComplete) {
                recordUtils.startOrComplete()
            } else {
                player.playOrPause(recordUtils.saveFile, playerListener)
            }
        }

        tvSaveRecord.setOnClickListener {
            if (!isComplete) {
                isComplete = true
                recordUtils.stopFullRecord()
            } else {
                player.pause()
            }
            realDismiss()
        }

        tvReRecord.setOnClickListener {
            recordUtils.stopFullRecord()
            recordUtils.reset()
        }
        mViewBinding.tvCancel.setOnClickListener {
            recordUtils.cleanPath()
            recordUtils.stopFullRecord()
            realDismiss()
        }
    }

    override fun showPop() {
        recordUtils.onReset()
        setOnDismissListener {
            if (isComplete) {
                recordUtils.saveFile?.let { onSuccess.invoke(it) }
            }
        }
        showAtLocation(mContext.window.decorView, Gravity.BOTTOM, 0, 0)
    }

    private fun onShowSuccessView() {
        isComplete = true
        mViewBinding.tvRecordTime.text = formatSeconds(maxTime)
        mViewBinding.tvRecordDuration.text = "/" + formatSeconds(maxTime)
        mViewBinding.tvTips.isVisible = false
        mViewBinding.ivRecordState.setImageResource(R.drawable.ic_record_audition)
        mViewBinding.tvState.text = "试听"
        mViewBinding.tvState.isVisible = true
    }


    private fun realDismiss() {
        AndroidSchedulers.mainThread().scheduleDirect({
            dismiss()
        }, 200, TimeUnit.MILLISECONDS)
    }

    override fun dismissOnDestroy() {
        super.dismissOnDestroy()
        recordUtils.destroy()
        player.stopPlay()
    }
}