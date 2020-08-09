package me.shetj.custom.base

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup

/**
 * [onMeasure]会在初始化之后,调用一到多次来测量控件或其中的子控件的宽高；
 * [onLayout] 会在[onMeasure]方法之后,被调用一次，将控件或其子控件进行布局；
 * [onDraw]   会在[onLayout]方法之后,调用一次，也会在用户手指触摸屏幕时被调用多次，来绘制控件。
 *
 * [measureChildren] 会触发子布局绘制
 * [measureChild]是对单个view进行测量
 */
open class BaseCustomViewGroup
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ViewGroup(context, attrs, defStyle) {

    protected val TAG = this.javaClass.simpleName

    //最新滑动距离
    private val mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //将所有的子View进行测量，这会触发每个子View的onMeasure函数
        //注意要与measureChild区分，measureChild是对单个view进行测量
        measureChildren(widthMeasureSpec, heightMeasureSpec)

        val measureHeight = measureHeight(heightMeasureSpec, getMaxHeight())
        val measureWidth = measureWidth(widthMeasureSpec, getMaxWidth())
        setMeasuredDimension(measureHeight, measureWidth)

    }

    /***
     * 默认：获取子View中宽度最大的值
     */
    open fun getMaxWidth(): Int {
        val childCount = childCount
        var maxWidth = 0
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            if (childView.measuredWidth > maxWidth) maxWidth = childView.measuredWidth
        }
        return maxWidth
    }


    /***
     * 默认： 将所有子View的高度相加
     */
    open fun getMaxHeight(): Int {
        val childCount = childCount
        var height = 0
        for (i in 0 until childCount) {
            val childView: View = getChildAt(i)
            height += childView.measuredHeight
        }
        return height
    }

    open fun measureHeight(measureSpec: Int, defaultSize: Int = suggestedMinimumHeight): Int {
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

    open fun measureWidth(measureSpec: Int, defaultSize: Int = suggestedMinimumWidth): Int {
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
        //需要对子布局进行layout child.layout
    }


    /**
     * 触摸监听
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    /**
     * 绘制
     */
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}
