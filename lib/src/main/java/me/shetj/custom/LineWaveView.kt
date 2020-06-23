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
import kotlin.math.max

class LineWaveView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    BaseCustomView(context, attrs, defStyle) {

    private var evenNumberAnimator: ValueAnimator? = null
    private val DEFAULT_TEXT: String = "请录音"
    private val LINE_WIDTH = 5f
    private val UPDATE_INTERVAL_TIME = 500L//500ms一次动画

    private var minLineHeight = 4f
    private var maxLineHeight = 12f

    private var oddNumber = 0f //基数
    private var oddNumber2 = 0f //基数
    private var evenNumber = 0f //偶数
    private var evenNumber2 = 0f //偶数

    private var text = DEFAULT_TEXT
    private var updateSpeed: Long = UPDATE_INTERVAL_TIME
    private var lineColor: Int = Color.GREEN
    private var textColor: Int = Color.BLACK
    private var lineWidth: Float = 15f
    private var textSize: Float = dip2px(20f).toFloat()

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
                textColor = getColor(
                    R.styleable.LineWaveView_voiceTextColor,
                    ContextCompat.getColor(context, R.color.app_color_theme_1)
                )
                text = getString(R.styleable.LineWaveView_text) ?: ""
                lineWidth = getDimension(R.styleable.LineWaveView_voiceLineWith, LINE_WIDTH)
                textSize = getDimension(R.styleable.LineWaveView_voiceTextSize, 42f)
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
                    evenNumber2 = maxLineHeight - changeSize + 4f
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

    fun setText(text: String) {
        this.text = text
        postInvalidate()
    }

    fun setTextColor(@ColorInt color: Int) {
        this.textColor = color
        postInvalidate()
    }

    fun setTextSize(dp: Float) {
        this.textSize = dip2px(dp).toFloat()
        postInvalidate()
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
        return (paint.measureText(text) + lineWidth * 14 * 3).toInt()
    }

    override fun getSuggestedMinimumHeight(): Int {
        return max(maxLineHeight * lineWidth , textSize).toInt()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopRecord()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            //获取实际宽高的一半
            val widthCentre = width / 2
            val heightCentre = height / 2
            paint.strokeWidth = 0f
            paint.color = textColor
            paint.textSize = textSize
            val textWidth = paint.measureText(text) + 10f
            canvas.drawText(
                text,
                widthCentre - textWidth / 2 + 5,
                heightCentre - (paint.ascent() + paint.descent()) / 2,
                paint
            )

            //设置颜色
            paint.color = lineColor
            //填充内部
            paint.style = Paint.Style.FILL
            //设置抗锯齿
            paint.isAntiAlias = true
            for (i in 0 until 7) {
                val number = when (i % 7) {
                    0 -> oddNumber2
                    1 -> evenNumber
                    2 -> oddNumber
                    3 -> evenNumber2
                    4 -> oddNumber
                    5 -> evenNumber
                    6 -> oddNumber2
                    else -> evenNumber2
                }

                rectRight.left = widthCentre.toFloat() + textWidth / 2 + (1 + 3 * i) * lineWidth
                rectRight.top = heightCentre - lineWidth * number / 2
                rectRight.right = widthCentre.toFloat() + textWidth / 2 + (2 + 3 * i) * lineWidth
                rectRight.bottom = heightCentre + lineWidth * number / 2

                //左边矩形
                rectLeft.left = widthCentre.toFloat() - textWidth / 2 - (2 + 3 * i) * lineWidth
                rectLeft.top = heightCentre - number * lineWidth / 2
                rectLeft.right = widthCentre.toFloat() - textWidth / 2 - (1 + 3 * i) * lineWidth
                rectLeft.bottom = heightCentre + number * lineWidth / 2

                canvas.drawRoundRect(rectRight, 6f, 6f, paint)
                canvas.drawRoundRect(rectLeft, 6f, 6f, paint)
            }
        }

    }
}