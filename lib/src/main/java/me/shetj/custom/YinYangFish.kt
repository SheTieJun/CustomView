package me.shetj.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import me.shetj.custom.base.BaseCustomView
import kotlin.math.min


class YinYangFish @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    BaseCustomView(context, attrs, defStyle) {

    private val mDefaultPaint1 = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
        style = Paint.Style.FILL
    }

    private val mDefaultPaint2 = Paint().apply {
        isAntiAlias = true
        color = Color.WHITE
        style = Paint.Style.FILL
        strokeWidth = 5f
    }

    private val mDefaultPaint3 = Paint().apply {
        isAntiAlias = true
        color = Color.GRAY
        style = Paint.Style.STROKE
    }

    private var path1: Path = Path()
    private var path2: Path = Path()
    private var path3: Path = Path()
    private var path4: Path = Path()

    private val valueAnimator = ValueAnimator.ofFloat(0f, 359f)?.apply {
        addUpdateListener {
            val rotation = it.animatedValue as Float
            updateRotation(rotation)
        }
        interpolator = LinearInterpolator()
        repeatCount = -1
        duration = 5000
    }
    @Synchronized
    fun pause(){
        valueAnimator?.takeIf {
            it.isRunning
        }?.apply {
            valueAnimator.pause()
        }
    }

    @Synchronized
    fun resume(){
        valueAnimator?.takeIf {
            !it.isRunning
        }?.apply {
            if (isStarted) {
                valueAnimator.resume()
            }else{
                valueAnimator.start()
            }
        }
    }

    private fun updateRotation(rotation: Float) {
        this.rotation = rotation
    }

    //测量
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = measureWidth(widthMeasureSpec)
        val height = measureHeight(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    //PathDirectionCCW 逆时针
    //PathDirectionCW  顺时针
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val halfWidth = ((width - paddingStart - paddingEnd) / 2).toFloat()
        val halfHeight = ((height - paddingTop - paddingBottom) / 2).toFloat()
        val minSize = min(halfWidth, halfHeight)
        path1.addCircle(
            halfWidth + paddingStart,
            halfHeight + paddingTop,
            minSize,
            Path.Direction.CW
        )
        path2.addRect(
            halfWidth + paddingStart,
            0f + paddingTop,
            minSize * 2 + halfWidth + paddingStart,
            minSize * 2 + paddingTop * 2,
            Path.Direction.CW
        )
        path3.addCircle(
            halfWidth + paddingStart,
            halfHeight + paddingTop - minSize / 2,
            minSize / 2,
            Path.Direction.CW
        );
        path4.addCircle(
            halfWidth + paddingStart,
            halfHeight + minSize / 2 + paddingTop,
            minSize / 2,
            Path.Direction.CCW
        );
        path1.op(path2, Path.Op.DIFFERENCE); //去掉path2
        path1.op(path3, Path.Op.UNION);//包含全部Path1和path3
        path1.op(path4, Path.Op.DIFFERENCE);//取得不同

        canvas?.drawPath(path1, mDefaultPaint1);

        canvas?.drawCircle(
            halfWidth + paddingStart,
            (halfHeight - minSize / 2) + paddingTop,
            minSize / 6, mDefaultPaint2
        );

        canvas?.drawCircle(
            halfWidth + paddingStart,
            halfHeight + minSize / 2 + paddingTop,
            minSize / 6, mDefaultPaint1
        );

        canvas?.drawCircle(
            halfWidth + paddingStart,
            halfHeight + paddingTop,
            minSize, mDefaultPaint3
        )
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        valueAnimator?.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        valueAnimator?.cancel()
    }
}