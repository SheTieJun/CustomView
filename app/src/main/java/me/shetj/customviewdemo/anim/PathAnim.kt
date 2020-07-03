package me.shetj.customviewdemo.anim

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Path
import android.view.View
import android.widget.ImageView
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.customviewdemo.R
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
}