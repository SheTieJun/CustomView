package me.shetj.customviewdemo.recorder

import android.Manifest
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import me.shetj.base.ktx.hasPermission
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.databinding.RecordLayoutPopupBinding
import me.shetj.recorder.core.SimRecordListener
import java.util.concurrent.TimeUnit


typealias Success = (file: String) -> Unit


class RecorderPopup(private val mContext: AppCompatActivity, private val onSuccess: Success) :
    BasePopupWindow<RecordLayoutPopupBinding>(mContext) {

    private val TIME = (30 * 60 * 1000).toLong()

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
            mViewBinding.tvReRecord.isVisible = true
            mViewBinding.tvSaveRecord.isVisible = true
            mViewBinding.llTime.isVisible = true
            mViewBinding.tvTips.isVisible = false
            mViewBinding.ivRecordState.setImageResource(R.drawable.ic_record_pause)
        }

        override fun autoComplete(file: String, time: Long) {
            super.autoComplete(file, time)
            onShowSuccessView(file)
        }

        override fun onRecording(time: Long, volume: Int) {
            super.onRecording(time, volume)
            mViewBinding.tvRecordTime.text = formatSeconds(time / 1000)
        }


        override fun onSuccess(file: String, time: Long) {
            super.onSuccess(file, time)
            onShowSuccessView(file)
        }

        override fun onReset() {
            isComplete = false
            mViewBinding.tvTips.isVisible = true
            mViewBinding.tvTips.text = "点击下方按钮开始录音"
            mViewBinding.llTime.isVisible = false
            mViewBinding.tvReRecord.isVisible = false
            mViewBinding.tvSaveRecord.isVisible = false
            mViewBinding.tvState.text = ""
            mViewBinding.tvRecordTime.text =formatSeconds(0)
            mViewBinding.tvRecordDuration.text = "/" + formatSeconds(TIME)
            mViewBinding.ivRecordState.setImageResource(R.drawable.ic_record_start)
        }

        override fun needPermission() {
            super.needPermission()
            mContext.hasPermission(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, isRequest = true
            )
        }

        override fun onRemind(mDuration: Long) {
            super.onRemind(mDuration)

        }
    }

    private val recordUtils by lazy {
        MixRecordUtils(listener).apply {
            setMaxProgress(TIME)
        }
    }

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
            }else {
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
            if (isComplete){
                recordUtils.saveFile?.let { onSuccess.invoke(it) }
            }
        }
        showAtLocation(mContext.window.decorView, Gravity.BOTTOM, 0, 0)
    }

    private fun onShowSuccessView(file: String) {
        isComplete = true
        mViewBinding.ivRecordState.setImageResource(R.drawable.ic_record_audition)
        mViewBinding.tvState.text = "试听"
        mViewBinding.tvState.isVisible = true
    }


    fun realDismiss(){
        AndroidSchedulers.mainThread().scheduleDirect({
            dismiss()
        },200,TimeUnit.MILLISECONDS)
    }

    override fun dismissOnDestroy() {
        super.dismissOnDestroy()
        player.stopPlay()
    }
}