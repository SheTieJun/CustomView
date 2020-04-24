package me.shetj.custom.base

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View


open class BaseCustomView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
        View(context, attrs, defStyle){


    fun measureHeight(measureSpec: Int, defaultSize: Int): Int {
        var result = 0
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = defaultSize +paddingTop +paddingBottom
            if (specMode == MeasureSpec.AT_MOST){
                result = result.coerceAtMost(specSize)
            }
        }
        result = result.coerceAtLeast(suggestedMinimumHeight)
        return result
    }

    fun measureWidth(measureSpec: Int, defaultSize: Int): Int {
        var result = 0
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
            dpVal, context.resources.displayMetrics).toInt()
    }
}

/**
 * onMeasure()会在初始化之后调用一到多次来测量控件或其中的子控件的宽高；
 *   onLayout()会在onMeasure()方法之后被调用一次，将控件或其子控件进行布局；
 *  onDraw()会在onLayout()方法之后调用一次，也会在用户手指触摸屏幕时被调用多次，来绘制控件。
 */