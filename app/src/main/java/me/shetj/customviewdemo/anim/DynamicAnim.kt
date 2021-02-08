package me.shetj.customviewdemo.anim

import android.content.Context
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FlingAnimation
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.customviewdemo.databinding.LayouDyAnimBinding
import me.shetj.customviewdemo.utils.createDialog
import timber.log.Timber


object DynamicAnim {

    fun showDyAnim(context: Context) {

        createDialog(context, LayouDyAnimBinding.inflate(LayoutInflater.from(context))) { vb ->
            val flingx = FlingAnimation(vb.ivMove, DynamicAnimation.TRANSLATION_X).apply {
                this.setMinValue(0f)
                    .setMaxValue(vb.root.width.toFloat() - 20)
                    .setFriction(0.1f)
                    .addEndListener { animation, canceled, value, velocity ->
                        if (velocity != 0f) {
                            setStartVelocity(-velocity).start()
                        }
                    }
                    .addUpdateListener { animation, value, velocity ->

                    }
            }
            val flingy = FlingAnimation(vb.ivMove, DynamicAnimation.TRANSLATION_Y).apply {
                this.setMinValue(0f)
                .setMaxValue(vb.root.height.toFloat() - 20)
                .setFriction(0.1f)
                .addEndListener { animation, canceled, value, velocity ->
                    Timber.tag("DynamicAnim").i("value：$value,velocity:$velocity")
                    if (velocity != 0f) {
                        setStartVelocity(-velocity).start()
                    }
                }
                .addUpdateListener { animation, value, velocity ->

                }
            }
            val gesture = GestureDetector(context, object : GestureDetector.OnGestureListener {
                override fun onDown(e: MotionEvent?): Boolean {
                    return true
                }

                override fun onShowPress(e: MotionEvent?) {
                }

                override fun onSingleTapUp(e: MotionEvent?): Boolean {
                    return false
                }

                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent?,
                    distanceX: Float,
                    distanceY: Float
                ): Boolean {
                    return true
                }

                override fun onLongPress(e: MotionEvent?) {
                }

                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent?,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    if (flingx.isRunning) {
                        flingx.setStartVelocity(velocityX)
                    }else{
                        flingx.setStartVelocity(velocityX).setMinValue(0f)
                            .setMaxValue(vb.root.width.toFloat() - 20)
                            .setFriction(0.2f).start()
                    }
                    if (flingy.isRunning) {
                        flingy.setStartVelocity(velocityY)
                    }else{
                        flingy.setStartVelocity(velocityY).setMinValue(0f)
                            .setMaxValue(vb.root.width.toFloat() - 20)
                            .setFriction(0.2f).start()
                    }
                    return true
                }
            })
            vb.btnStart.setOnClickListener {
                if (flingx.isRunning) {
                    flingx.setStartVelocity(500f)
                } else {
                    flingx.setStartVelocity(500f).setMinValue(0f)
                        .setMaxValue(vb.root.width.toFloat() - 20)
                        .setFriction(0.1f).start()
                }
            }

            vb.root.setOnTouchListener { v, event ->

                return@setOnTouchListener gesture.onTouchEvent(event)
            }


        }
    }
}