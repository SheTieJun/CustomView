package me.shetj.custom.base

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.graphics.withClip

/**
 * onMeasure()会在初始化之后,调用一到多次来测量控件或其中的子控件的宽高；
 * onLayout() 会在onMeasure()方法之后,被调用一次，将控件或其子控件进行布局；
 * onDraw()   会在onLayout()方法之后,调用一次，也会在用户手指触摸屏幕时被调用多次，来绘制控件。
 */
open class BaseCustomView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    View(context, attrs, defStyle) {

    protected val TAG = this.javaClass.simpleName

    //最新滑动距离
    private val mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

    /**
     * 父容器传过来
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw = paddingLeft + paddingRight + suggestedMinimumWidth
        val w = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val minh = paddingBottom + paddingTop + suggestedMinimumHeight
        val h = resolveSizeAndState(minh, heightMeasureSpec, 0)
        setMeasuredDimension(w, h)
//        计算自己所需高度、宽度则通过重写getSuggestedMinimumWidth，getSuggestedMinimumHeight来进行计算
    }

    fun measureHeight(measureSpec: Int, defaultSize: Int = suggestedMinimumHeight): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = defaultSize + paddingTop + paddingBottom
            if (specMode == MeasureSpec.AT_MOST) {
                result = result.coerceAtMost(specSize)
            }
        }
        result = result.coerceAtLeast(suggestedMinimumHeight)
        return result
    }

    fun measureWidth(measureSpec: Int, defaultSize: Int = suggestedMinimumWidth): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = defaultSize + paddingLeft + paddingRight
            if (specMode == MeasureSpec.AT_MOST) {
                result = result.coerceAtMost(specSize)
            }
        }
        result = result.coerceAtLeast(suggestedMinimumWidth)
        return result
    }

    fun dip2px(dpVal: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpVal, context.resources.displayMetrics
        ).toInt()
    }

    /**
     * XML 加载结束
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    /**
     * 视频大小变化的时候
     */
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }


    /**
     * 位置变化
     */
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }


    /**
     * 触摸监听
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return super.onTouchEvent(event)
    }

    /**
     * 绘制
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    /**
     * 重写这个方法，可以剪切整个视图
     */
    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
    }
}
