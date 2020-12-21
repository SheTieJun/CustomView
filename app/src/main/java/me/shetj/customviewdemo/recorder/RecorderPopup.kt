package me.shetj.customviewdemo.recorder

import android.Manifest
import android.transition.TransitionManager
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import me.shetj.base.ktx.hasPermission
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.databinding.RecordLayoutPopupBinding
import me.shetj.dialog.OrangeDialog
import me.shetj.recorder.core.SimRecordListener
import java.util.concurrent.TimeUnit


typealias Success = (file: String) -> Unit


class RecorderPopup(private val mContext: AppCompatActivity, private var onSuccess: Success? = null) :
        BasePopupWindow<RecordLayoutPopupBinding>(mContext) {

    private var remindDialog: OrangeDialog? =null
    private val maxTime = (30 * 60 * 1000).toLong()

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
            mViewBinding.tvTips.text = "录音中，30分钟后自动保存"
            mViewBinding.ivRecordState.setImageResource(R.drawable.ic_record_pause)
//            mViewBinding.spreadView.start = true
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
        }

        override fun onError(e: Exception) {
            super.onError(e)
            onReset()
        }
    }

    private val recordUtils by lazy { MixRecordUtils(maxTime, listener) }

    private val player: AudioPlayer by lazy { AudioPlayer() }


    fun setOnSuccess(onSuccess: Success?) {
        this.onSuccess = onSuccess
    }

    override fun audioLoss() {
        super.audioLoss()
        player.pause()
        recordUtils.stopFullRecord()
    }

    override fun RecordLayoutPopupBinding.initUI() {
        ivRecordState.setOnClickListener {
            if (!isComplete) {
                recordUtils.startOrComplete()
                requestAudioFocus()
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
            if (isComplete||recordUtils.hasRecord()) {
                showRemindTip()
            }else{
                dismiss()
            }
        }
    }

    /**
     * activity 返回键判断
     */
    fun onBackPress(): Boolean {
        return if (recordUtils.hasRecord()||isComplete ) {
            showRemindTip()
            false
        }else{
            true
        }
    }

    private fun showRemindTip() {
        (remindDialog ?: OrangeDialog.Builder(mViewBinding.root.context)
                .setTitle("是否保存录音")
                .setNegativeText("取消")
                .setOnNegativeCallBack {  _, _ ->
                    recordUtils.cleanPath()
                    recordUtils.stopFullRecord()
                    realDismiss()
                }
                .setPositiveText("保存")
                .setonPositiveCallBack {  _, _ ->
                    if (!isComplete) {
                        isComplete = true
                        recordUtils.stopFullRecord()
                    }
                    realDismiss()
                }.build().also {
                    remindDialog = it
                }).show()
    }

    override fun showPop() {
        recordUtils.onReset()
        setOnDismissListener {
            if (isComplete) {
                recordUtils.saveFile?.let { onSuccess?.invoke(it) }
            }
            isComplete = false
        }
        showAtLocation(mContext.window.decorView, Gravity.BOTTOM, 0, 0)
    }

    private fun onShowSuccessView() {
        if (isShowing) {
            isComplete = true
        }
//        mViewBinding.spreadView.start = false
        mViewBinding.tvTips.isVisible = false
        mViewBinding.ivRecordState.setImageResource(R.drawable.ic_record_audition)
        mViewBinding.tvState.text = "试听"
        mViewBinding.tvState.isVisible = true
    }


    private fun realDismiss() {
        AndroidSchedulers.mainThread().scheduleDirect({
            dismiss()
        }, 50, TimeUnit.MILLISECONDS)
    }

    override fun dismissStop() {
        player.pause()
        recordUtils.stopFullRecord()
//        super.dismissStop()
    }

    override fun dismissOnDestroy() {
        super.dismissOnDestroy()
        recordUtils.destroy()
        player.stopPlay()
    }
}