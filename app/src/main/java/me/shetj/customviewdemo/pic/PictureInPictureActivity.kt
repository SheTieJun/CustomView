package me.shetj.customviewdemo.pic

import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_picture_in_picture.*
import me.shetj.customviewdemo.R

/**
 * 1. 设置xml
 *      android:resizeableActivity="true"
android:supportsPictureInPicture="true"
 */
class PictureInPictureActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val pictureInPictureParams: PictureInPictureParams.Builder =
        PictureInPictureParams.Builder()

    private val mReceiver:PicBroadcastReceiver = PicBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_in_picture)

    }

    //    onUserLeaveHint： 作为Activity的生命周期回调的部分,会在用户决定将Activity放到后台时被调用
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (iWantToBeInPipModeNow() && hasPicFeature()) {
                enterPictureInPictureMode(
                    PictureInPictureParams.Builder()
                        .apply {
                            //可以添加控件
                            setAspectRatio(Rational(16, 9)) //设置比例
                        }.build()
                )
            }
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
}