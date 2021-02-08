package me.shetj.customviewdemo.pic

import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import me.shetj.base.mvp.BaseBindingActivity
import me.shetj.base.mvp.BasePresenter
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.databinding.ActivityPictureInPictureBinding

/**
 * 1. 设置xml
 *      android:resizeableActivity="true"
android:supportsPictureInPicture="true"
 */
class PictureInPictureActivity : BaseBindingActivity<BasePresenter<*>,ActivityPictureInPictureBinding>(),
    PicBroadcastReceiver.PicCtrlListener {

    @RequiresApi(Build.VERSION_CODES.O)
    private val pictureInPictureParams: PictureInPictureParams.Builder =
        PictureInPictureParams.Builder()
    private val mReceiver: PicBroadcastReceiver = PicBroadcastReceiver()
    private var orientationUtils: OrientationUtils? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ArmsUtils.statuInScreen2(this, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && iWantToBeInPipModeNow()) {
           mViewBinding.enter.setOnClickListener {
                enterPic()
            }
        } else {
            mViewBinding.enter .visibility = View.GONE
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    override fun initData() {
        mReceiver.addListener(this)
    }

    override fun initView() {
        initVideo()
    }

    private fun initVideo() {
        orientationUtils = OrientationUtils(this, mViewBinding.videoView)
        //初始化不打开外部的旋转
        orientationUtils?.isEnable = false
        val source1 =
            "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4"
        mViewBinding.videoView.setUp(source1, true, "测试视频")
        mViewBinding.videoView.startPlayLogic()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    /**
     * 进入画中画模式
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun enterPic() {
        if (hasPicFeature()) {
            enterPictureInPictureMode(
                pictureInPictureParams
                    .apply {
                        //可以添加控件
                        setAspectRatio(
                            Rational(16, 9)
                        ) //设置比例
                    }.build()
            )
        }
    }

    private fun iWantToBeInPipModeNow(): Boolean {
        return true
    }


    //在画中画期间处理界面
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        mViewBinding.enter.isVisible = !isInPictureInPictureMode
        if (isInPictureInPictureMode) {
            mReceiver.registerReceiver(this)
        } else {
            mReceiver.unRegister(this)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        mReceiver.removeListener(this)
        GSYVideoManager.releaseAllVideos()
    }

    override fun play() {
        super.play()
        GSYVideoManager.instance().start()
    }

    override fun close() {
        super.close()
        finish()
    }

    override fun pause() {
        super.pause()
        GSYVideoManager.instance().pause()
    }

}