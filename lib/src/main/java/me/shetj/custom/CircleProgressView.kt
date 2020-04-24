package me.shetj.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import me.shetj.custom.base.BaseCustomView


//测量 绘制
class CircleProgressView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    BaseCustomView(context, attrs, defStyle) {
    private val defColor = Color.YELLOW
    private var defWith: Float = dip2px(10f).toFloat()
    private val defaultSize = dip2px(88f)
    private var valueAnimator: ValueAnimator ?=null
    private var progressColor: Int = defColor
    private var backProgressWith: Float = defWith
    private var backProgressColor: Int = defColor
    private var progressWith: Float = backProgressWith + dip2px(4f)
    private var max = 100
    private var progress = 0

    private val mProgressPaint = Paint().apply {
        isAntiAlias = true
        color = Color.YELLOW
        strokeWidth = progressWith
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND //
        maskFilter = BlurMaskFilter(5f, BlurMaskFilter.Blur.SOLID)  //上左：内发光(INNER)
//        上右：外发光(SOLID)
//        下左：内外发光(NORMAL)
//        下右：仅显示发光效果(OUTER),该模式下仅会显示发光效果，会把原图像中除了发光部分，全部变为透明
    }

    private val mBgProgressPaint = Paint().apply {
        isAntiAlias = true
        color = Color.DKGRAY
        strokeWidth = backProgressWith
        style = Paint.Style.STROKE
    }

    init {
        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView).apply {
                backProgressColor = getColor(R.styleable.CircleProgressView_backProgressColor, Color.DKGRAY)
                backProgressWith =  getDimension(R.styleable.CircleProgressView_backProgressWith, defWith)
                progressColor = getColor(R.styleable.CircleProgressView_progressColor, Color.YELLOW)
                progressWith =  getDimension(R.styleable.CircleProgressView_progressWith, defWith)
                max = getInt(R.styleable.CircleProgressView_max,100)
                progress = getInt(R.styleable.CircleProgressView_progressSize,0)
                initConfig()
            }.recycle()
        }
    }

    private fun initConfig() {
        mProgressPaint.color = progressColor
        mProgressPaint.strokeWidth = progressWith
        mBgProgressPaint.color = backProgressColor
        mBgProgressPaint.strokeWidth = backProgressWith

    }

    //      LinearGradient gradient;//线型渐变色 一把只能在绘制横竖直线 或者矩形图形的时候效果好。
    //      RadialGradient gradient;//圆形渐变色 就像树了轮廓一样 一环套一环的着色 下一环的半径是比上一环半径的一个radius值
    //      BitmapShader gradient;  位图型渐变色，其实就相当于把位图当颜色。
    //      SweepGradient gradient;//角度渐变色
    //     ComposeShader gradient;//组合渐变色


    fun setProgress(progress: Int) {
        if (valueAnimator?.isRunning == true) {
            valueAnimator?.cancel()
        }
        startProgressAnim(this.progress,progress)
    }

    private fun startProgressAnim(start: Int, end: Int) {
        post {
            valueAnimator =  ValueAnimator.ofInt(start, end)?.apply {
                addUpdateListener {
                    progress = it.animatedValue as Int
                    postInvalidate()
                }
                interpolator = AccelerateDecelerateInterpolator()
                duration = 600
            }
            valueAnimator?.start()
        }
    }

    //测量
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = measureWidth(widthMeasureSpec,defaultSize)
        val height = measureHeight(heightMeasureSpec,defaultSize)
        setMeasuredDimension(width, height)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val fl = progressWith / 2 +5f
        //首先画背景圆
        canvas?.drawCircle(width/2f,height/2f,(width)/2f- fl,mBgProgressPaint)
        //再画上层
        val oval = RectF(fl, fl, width - fl, height-fl)
        val sweepGradient = LinearGradient(fl, fl,width - fl, height-fl, Color.parseColor("#FEDE47"), Color.parseColor("#FEBB22"),Shader.TileMode.REPEAT)
//        val sweepGradient = SweepGradient(width/2f,height/2f,Color.parseColor("#FEDE47"), Color.parseColor("#FEBB22"))
        mProgressPaint.shader = sweepGradient
        canvas?.drawArc(oval,-90f,progress/max.toFloat()*360,false,mProgressPaint)
    }
}