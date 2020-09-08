package me.shetj.customviewdemo.pic

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.annotation.RequiresApi
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import kotlinx.android.synthetic.main.activity_picture_in_picture.*
import me.shetj.base.mvp.BaseActivity
import me.shetj.base.mvp.BasePresenter
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.customviewdemo.R

/**
 * 1. 设置xml
 *      android:resizeableActivity="true"
android:supportsPictureInPicture="true"
 */
class PictureInPictureActivity : BaseActivity<BasePresenter<*>>(),
    PicBroadcastReceiver.PicCtrlListener {

    @RequiresApi(Build.VERSION_CODES.O)
    private val pictureInPictureParams: PictureInPictureParams.Builder =
        PictureInPictureParams.Builder()
    private val mReceiver: PicBroadcastReceiver = PicBroadcastReceiver()
    private var orientationUtils: OrientationUtils? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ArmsUtils.statuInScreen2(this,false)
        setContentView(R.layout.activity_picture_in_picture)
    }

    override fun initData() {
        mReceiver.addListener(this)
    }

    override fun initView() {
        initVideo()
    }

    private fun initVideo() {
        orientationUtils = OrientationUtils(this, videoView)
        //初始化不打开外部的旋转
        orientationUtils?.isEnable = false
        videoView?.setUp("https://vod.lycheer.net/e22cd48bvodtransgzp1253442168/d6b59e205285890789389180692/v.f20.mp4", false, null, "")
        videoView.startPlayLogic()
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && iWantToBeInPipModeNow()) {
            enterPic()
        }else{
            super.onBackPressed()
        }
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