package me.shetj.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import me.shetj.custom.base.BaseCustomView
import kotlin.math.min


class AngleSeekBar @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    BaseCustomView(context, attrs, defStyle) {
    private var globalRegion: Region?=null
    private var mStrokeColor = ContextCompat.getColor(context, R.color.app_color_theme_2)
    private var mProgressColor = ContextCompat.getColor(context, R.color.app_color_theme_3)
    private var mBgProgressColor = ContextCompat.getColor(context, R.color.app_color_theme_6)
    private var mStrokeWidth = 8f
    private var max = 100f
    private var progress = 10f
    private var isTouchDrag = false
    private var lastX: Float = 0.toFloat()
    private var lastY: Float = 0.toFloat()
    private val path = Path()
    private val bgPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND //
        color = mBgProgressColor
    }
    private val progressPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND //
        color = mProgressColor
        maskFilter = BlurMaskFilter(2f, BlurMaskFilter.Blur.SOLID)
    }
    private val cirPaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = mStrokeWidth
        isAntiAlias = true
        color = mStrokeColor
        maskFilter = BlurMaskFilter(2f, BlurMaskFilter.Blur.SOLID)
    }
    private val circleRegion = Region() //判断是否是点击特定的区域

    init {
        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.AngleSeekBar).apply {
                max = getInt(R.styleable.AngleSeekBar_android_max, 100).toFloat()
                progress = getInt(R.styleable.AngleSeekBar_android_progress, 0).toFloat()
                mStrokeColor = getColor(
                    R.styleable.AngleSeekBar_android_strokeColor,
                    ContextCompat.getColor(context, R.color.app_color_theme_3)
                )
                mStrokeWidth = getFloat(R.styleable.AngleSeekBar_android_strokeWidth, 0f)
                mProgressColor = getColor(
                    R.styleable.AngleSeekBar_progressColor,
                    ContextCompat.getColor(context, R.color.app_color_theme_2)
                )
                mBgProgressColor = getColor(
                    R.styleable.AngleSeekBar_backProgressColor,
                    ContextCompat.getColor(context, R.color.app_color_theme_6)
                )
                intConfig()
            }.recycle()
        }
    }

    private fun intConfig() {
        cirPaint.strokeWidth = mStrokeWidth
        cirPaint.color = mStrokeColor
        progressPaint.color = mProgressColor
        bgPaint.color = mBgProgressColor
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        globalRegion = Region(0, 0, w, h)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val mHeight = height - paddingLeft - paddingRight
        val halfHeight = mHeight / 2.toFloat() //大圆半径
        val mWidth = width - paddingStart - paddingEnd - halfHeight //实际手势宽度
        if (event.x - paddingStart - 5 >= 0 && event.x - paddingStart <= mWidth) {
            progress = min(100f, (event.x - paddingStart) * max / mWidth)
            invalidate()
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
                val x = event.x.toInt()
                val y = event.y.toInt()
//                 ▼点击区域判断
                if (circleRegion.contains(x, y)) {
                    isTouchDrag = true
                }
            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_UP -> {
                isTouchDrag = false
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val mHeight = height - paddingLeft - paddingRight
        val halfHeight = mHeight / 2.toFloat() //大圆半径
        val mWidth = width - paddingStart - paddingEnd - halfHeight //实际手势宽度
        //画背景

        path.reset()
        path.moveTo(paddingStart.toFloat(), halfHeight + paddingTop)
        path.lineTo(mWidth + paddingStart, paddingTop.toFloat())
        path.lineTo(mWidth + paddingStart, (mHeight + paddingTop).toFloat())
        path.close()
        canvas?.drawPath(path, bgPaint)

        path.reset()
        path.addCircle(
            width.toFloat() - halfHeight - paddingEnd,
            halfHeight + paddingTop,
            halfHeight,
            Path.Direction.CCW
        )
        canvas?.drawPath(path, bgPaint)

        //画进度
        val progressWidth = mWidth * progress / max //进度宽
        val processHeight = mHeight * progress / max //进度园的直径
        val startH = paddingTop.toFloat() + (mHeight - processHeight) / 2  //获取到
        val centerX = progressWidth + paddingStart
        val centerY = startH + processHeight / 2

        path.reset()
        path.moveTo(paddingStart.toFloat(), halfHeight + paddingTop)
        path.lineTo(centerX, startH)
        path.lineTo(centerX, (startH + processHeight))
        path.close()
        canvas?.drawPath(path, progressPaint)

        path.reset()
        path.addCircle(
            centerX, centerY, (processHeight / 2) + 3f, Path.Direction.CCW
        )
        canvas?.drawPath(path, cirPaint)

        path.reset()
        path.addCircle(
            centerX, centerY, (processHeight / 2) + 2f,
            Path.Direction.CCW
        )
        canvas?.drawPath(path, progressPaint)

        globalRegion?.let {
            path.reset()
            //画进度
            path.addCircle(centerX, centerY, halfHeight, Path.Direction.CCW)
            circleRegion.setPath(path, it);  //添加点击区域，后续添加点击态
        }
    }
}