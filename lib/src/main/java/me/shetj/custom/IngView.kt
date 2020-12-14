package me.shetj.custom

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.REVERSE
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import me.shetj.custom.base.BaseCustomView
import kotlin.math.min

class IngView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    BaseCustomView(context, attrs, defStyle) {

    private var evenNumberAnimator: ValueAnimator? = null
    private val LINE_WIDTH = 5f
    private val UPDATE_INTERVAL_TIME = 500L//500ms一次动画

    private var minLineHeight = dip2px(2f).toFloat()
    private var maxLineHeight = dip2px(10f).toFloat()

    private var oddNumber = 0f //基数
    private var oddNumber2 = 0f //基数
    private var evenNumber = 0f //偶数
    private var evenNumber2 = 0f //偶数

    private var updateSpeed: Long = UPDATE_INTERVAL_TIME
    private var lineColor: Int = Color.GREEN
    private var lineWidth: Float = 3f

    private val paint = Paint()
    private val rectRight = RectF()//右边波纹矩形的数据，10个矩形复用一个rectF
    private val rectLeft = RectF()//左边波纹矩形的数据


    init {
        initView(attrs, context)
    }

    private fun initView(attrs: AttributeSet?, context: Context) {
        context.obtainStyledAttributes(attrs, R.styleable.LineWaveView)
            .apply {
                lineColor = getColor(
                    R.styleable.LineWaveView_voiceLineColor,
                    ContextCompat.getColor(context, R.color.app_color_theme_1)
                )
                lineWidth = getDimension(R.styleable.LineWaveView_voiceLineWith, LINE_WIDTH)
                updateSpeed = getInt(R.styleable.LineWaveView_updateSpeed, 500).toLong()
                recycle()
            }

        evenNumberAnimator = ObjectAnimator.ofFloat(minLineHeight, maxLineHeight)
            .apply {
                addUpdateListener {
                    val changeSize = it.animatedValue as Float
                    oddNumber = changeSize * 0.9f
                    oddNumber2 = changeSize * 0.65f
                    evenNumber = maxLineHeight - changeSize + 2f
                    evenNumber2 = maxLineHeight - changeSize
                    postInvalidate()
                }
                repeatMode = REVERSE
                repeatCount = -1
                duration = updateSpeed
            }
    }

    @Synchronized
    fun startRecord() {
        evenNumberAnimator?.takeIf {
            !it.isRunning
        }?.apply {
            oddNumber = minLineHeight //基数
            evenNumber = maxLineHeight - 2f  //偶数
            oddNumber2 = minLineHeight
            evenNumber2 = maxLineHeight  //偶数
            evenNumberAnimator?.start()
        }
    }

    @Synchronized
    fun stopRecord() {
        evenNumberAnimator?.takeIf {
            it.isRunning
        }?.apply {
            oddNumber = 0f //基数
            evenNumber = 0f //偶数
            oddNumber2 = 0f
            evenNumber2 = 0f
            evenNumberAnimator?.cancel()
        }
    }


    fun setLineColor(@ColorInt color: Int) {
        this.lineColor = color
        postInvalidate()
    }


    fun setLineWidth(dp: Float) {
        this.lineWidth = dip2px(dp).toFloat()
        postInvalidate()
    }

    fun updateSpeed(updateSpeed: Long) {
        this.updateSpeed = updateSpeed
        evenNumberAnimator?.duration = updateSpeed
    }


    override fun getSuggestedMinimumWidth(): Int {
        return (lineWidth * 14 * 3).toInt()
    }

    override fun getSuggestedMinimumHeight(): Int {
        return maxLineHeight.toInt()+2
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startRecord()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopRecord()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            val widthCentre = width/2 - (2 + 2 * 2) * lineWidth
            val heightCentre = height
            paint.strokeWidth = 0f

            //设置颜色
            paint.color = lineColor
            //填充内部
            paint.style = Paint.Style.FILL
            //设置抗锯齿
            paint.isAntiAlias = true
            for (i in 0 until 4) {
                val number = when (i % 4) {
                    0 -> oddNumber2
                    1 -> evenNumber
                    2 -> oddNumber
                    else -> evenNumber2
                }

                rectRight.left = widthCentre + (1 + 2 * i) * lineWidth
                rectRight.top = heightCentre -  number
                rectRight.right = widthCentre + (2 + 2 * i) * lineWidth
                rectRight.bottom = heightCentre.toFloat()+2f

                canvas.drawRoundRect(rectRight, 6f, 6f, paint)
                canvas.drawRoundRect(rectLeft, 6f, 6f, paint)
            }
        }

    }
}