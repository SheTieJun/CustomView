package me.shetj.customviewdemo.anim

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.custom.base.BaseCustomView


class StrokeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseCustomView(context, attrs, defStyleAttr) {

    private var animator: ObjectAnimator? = null
    private val path = Path()
    private val paint = Paint().apply {
        strokeWidth = ArmsUtils.dp2px(2f).toFloat()
        style = Paint.Style.STROKE
    }

    fun setBgColor(color: Int) {
        paint.color = color
        postInvalidate()
    }


    fun startAni(startColor: Int, endColor: Int) {
        animator =
            ObjectAnimator.ofInt(this, "bgColor", startColor, endColor)
        animator!!.duration = 500
        animator!!.setEvaluator(ArgbEvaluator())
        animator!!.start()
    }

    fun stopAni() {
        animator?.cancel()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        path.reset()
        path.moveTo(0f, 0f)
        path.lineTo(width.toFloat(), 0f)
        path.lineTo(width.toFloat(), height.toFloat())
        path.lineTo(0f, height.toFloat())
        path.close()
        canvas?.drawPath(path, paint)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAni()
    }

}