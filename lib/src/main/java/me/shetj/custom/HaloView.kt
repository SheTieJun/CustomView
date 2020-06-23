package me.shetj.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import kotlin.math.min

/**
 * 圆形呼吸录音状态
 */
class HaloView : AppCompatTextView {

    private var valueAnimator: ValueAnimator? = null
    private lateinit var mPaint: Paint
    private lateinit var mBGPaint: Paint
    private var haloColor: Int = Color.RED
    private var haloSize: Float = 0f
    private var haloBackgroundColor: Int = Color.RED

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        context.obtainStyledAttributes(
            attrs, R.styleable.HaloView, defStyle, 0
        ).apply {
            haloColor = getColor(
                R.styleable.HaloView_haloColor,
                ContextCompat.getColor(context, R.color.app_color_theme_1)
            )
            haloBackgroundColor = getColor(
                R.styleable.HaloView_haloBackgroundColor,
                ContextCompat.getColor(context, R.color.app_color_theme_1)
            )
            haloSize = getDimension(
                R.styleable.HaloView_haloSize,
                10f
            )
            if (haloSize < 10) {
                haloSize = 10f
            }
            recycle()
        }
        mPaint = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            isAntiAlias = true
            color = haloColor
            style = Paint.Style.FILL
            maskFilter = BlurMaskFilter(haloSize, BlurMaskFilter.Blur.OUTER)
        }
        mBGPaint = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            isAntiAlias = true
            color = haloBackgroundColor
            style = Paint.Style.FILL
        }
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        valueAnimator = ValueAnimator.ofFloat(8f, haloSize).apply {
            duration = 600
            repeatMode = ValueAnimator.REVERSE
            repeatCount = -1
            addUpdateListener {
                val currentValue = it?.animatedValue
                setHaloSize(currentValue as Float)
            }
        }
    }

    @Synchronized
    fun startAnim() {
        if (!isStart()) {
            valueAnimator?.start()
        }
    }

    fun isStart(): Boolean {
        return valueAnimator?.isRunning ?: false
    }

    @Synchronized
    fun stopAnim() {
        valueAnimator?.cancel()
        setHaloSize(0.1f)
    }

    private fun setHaloSize(size: Float) {
        mPaint.maskFilter = BlurMaskFilter(size, BlurMaskFilter.Blur.OUTER)
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        val halfWidth = ((width - paddingStart - paddingEnd) / 2).toFloat()
        val halfHeight = ((height - paddingTop - paddingBottom) / 2).toFloat()
        val minSize = min(halfWidth, halfHeight)
        canvas.drawCircle(
            (halfWidth + paddingStart),
            (halfHeight + paddingTop),
            minSize - haloSize,
            mPaint
        )
        canvas.drawCircle(
            (halfWidth + paddingStart),
            (halfHeight + paddingTop),
            minSize - haloSize,
            mBGPaint
        )
        super.onDraw(canvas)
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startAnim()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnim()
    }
}