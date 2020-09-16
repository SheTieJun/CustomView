package me.shetj.customviewdemo.anim

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Path
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.base.tools.app.FragmentUtils
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.fragment.VideoFragment
import me.shetj.customviewdemo.utils.createDialog


/**
 * ptah 动画
 */
object PathAnim {
    fun showPathAnim(context: Context) {

        createDialog(context, R.layout.layou_path_anim) {
            val imageView = it.findViewById<ImageView>(R.id.iv_move)
            val path: Path = Path()
            path.moveTo(0f, 0f)

            path.quadTo(
                ArmsUtils.dip2px(100f).toFloat(), ArmsUtils.dip2px(70f).toFloat(),
                ArmsUtils.dip2px(50f).toFloat(), ArmsUtils.dip2px(140f).toFloat()
            )

            path.lineTo(ArmsUtils.dip2px(100f).toFloat(), ArmsUtils.dip2px(70f).toFloat())

            path.quadTo(
                ArmsUtils.dip2px(100f).toFloat(), ArmsUtils.dip2px(320f).toFloat(),
                ArmsUtils.dip2px(140f).toFloat(), ArmsUtils.dip2px(180f).toFloat()
            )

            path.quadTo(
                ArmsUtils.dip2px(140f).toFloat(), ArmsUtils.dip2px(140f).toFloat(),
                ArmsUtils.dip2px(180f).toFloat(), ArmsUtils.dip2px(230f).toFloat()
            )
            path.lineTo(ArmsUtils.dip2px(230f).toFloat(), ArmsUtils.dip2px(100f).toFloat())

            path.quadTo(
                ArmsUtils.dip2px(100f).toFloat(), ArmsUtils.dip2px(50f).toFloat(),
                ArmsUtils.dip2px(200f).toFloat(), ArmsUtils.dip2px(0f).toFloat()
            )

            path.lineTo(ArmsUtils.dip2px(300f).toFloat(), ArmsUtils.dip2px(0f).toFloat())

            val animator = imageView.getPathAnimator(path)
            it.findViewById<View>(R.id.btn_start).setOnClickListener {
                animator.start()
            }

        }
    }

    fun ImageView.getPathAnimator(path: Path): ObjectAnimator {
        return ObjectAnimator.ofFloat(this, "x", "y", path).apply {
            duration = 10000
        }
    }


    fun showFragmentAnim(context: Context,fragmentManager: FragmentManager){
        var videoPlayer :StandardGSYVideoPlayer?=null
        var touch:View?=null
        createDialog(context = context,R.layout.layout_test_fragment_dialog){
            videoPlayer = it.findViewById(R.id.videoPlayer)
            val source1 =
                "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4"
            videoPlayer!!.setUp(source1, true, "测试视频")
            videoPlayer!!.startPlayLogic()
            touch = it.findViewById<View>(R.id.touch_outside)

        }?.apply {
            touch?.setOnClickListener {
                FragmentUtils.add(fragmentManager,
                    VideoFragment.newInstance(),
                    R.id.root,
                    sharedElements = arrayOf(videoPlayer!!)
                )
                this.dismiss()
            }
            setOnDismissListener {
                videoPlayer?.release()
            }
        }
    }
}