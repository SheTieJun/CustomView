package me.shetj.customviewdemo.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import me.shetj.base.ktx.logi
import timber.log.Timber


class Title2Behavior @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null):CoordinatorLayout.Behavior<View>() {



    override fun onDependentViewChanged(
            parent: CoordinatorLayout,
            child: View,
            dependency: View
    ): Boolean {
        Timber.tag("Title2Behavior").i("onDependentViewChanged")
        return super.onDependentViewChanged(parent, child, dependency)
    }

    /**
     * 是否依赖这个view
     */
    override fun layoutDependsOn(
            parent: CoordinatorLayout,
            child: View,
            dependency: View
    ): Boolean {
        Timber.tag("Title2Behavior").i("layoutDependsOn")
        return false
    }



    override fun onMeasureChild(
            parent: CoordinatorLayout,
            child: View,
            parentWidthMeasureSpec: Int,
            widthUsed: Int,
            parentHeightMeasureSpec: Int,
            heightUsed: Int
    ): Boolean {
        "onMeasureChild".logi()
        return super.onMeasureChild(
                parent,
                child,
                parentWidthMeasureSpec,
                widthUsed,
                parentHeightMeasureSpec,
                heightUsed
        )
    }


    /**
     * true ->
     * 1.onNestedPreScroll() 会在scrolling View获得滚动事件前调用，它允许你消费部分或者全部的事件信息。
     * 2.onNestedScroll() 会在scrolling View做完滚动后调用，通过回调可以知道scrolling view滚动了多少和它没有消耗的滚动事件。
     */
    override fun onStartNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: View,
            directTargetChild: View,
            target: View,
            axes: Int,
            type: Int
    ): Boolean {
        Timber.tag("Title2Behavior").i("onStartNestedScroll:axes:$axes,type:$type")
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    /**
     * 滑动之前
     */
    override fun onNestedPreScroll(
            coordinatorLayout: CoordinatorLayout,
            child: View,
            target: View,
            dx: Int,
            dy: Int,
            consumed: IntArray,
            type: Int
    ) {
        if (child.y > 0f && dy > 0){
            if (child.y - dy.toFloat() > 0){
                child.translationY = child.y - dy.toFloat()
            }else{
                child.translationY = 0f
            }
            return
        } else if (child.y < 0f && dy < 0){
            if (child.y - dy.toFloat() < 0){
                child.translationY = child.y - dy.toFloat()
            }else{
                child.translationY = 0f
            }
            return
        }else{
            Timber.tag("Title2Behavior").i("onNestedPreScroll:target:y:${child.y}:dx:$dx, dy:$dy, consumed:$consumed, type:$type")
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        }
    }


    override fun onNestedScroll(
            coordinatorLayout: CoordinatorLayout,
            child: View,
            target: View,
            dxConsumed: Int,
            dyConsumed: Int,
            dxUnconsumed: Int,
            dyUnconsumed: Int,
            type: Int,
            consumed: IntArray
    ) {
        Timber.tag("Title2Behavior").i("onNestedScroll: \n " +
                "               dxConsumed:$dxConsumed,\n" +
                "                dyConsumed:$dyConsumed,\n" +
                "                dxUnconsumed:$dxUnconsumed,\n" +
                "                dyUnconsumed:$dyUnconsumed,\n" +
                "                type:$type,")
        if (dyUnconsumed != 0) {
            if (child.y in -150f..150f && type == 0) {
                child.translationY = child.y - dyUnconsumed.toFloat()
            }
        }
        super.onNestedScroll(
                coordinatorLayout,
                child,
                target,
                dxConsumed,
                dyConsumed,
                dxUnconsumed,
                dyUnconsumed,
                type,
                consumed
        )
    }


    // 给 Behavior 设置 LayoutParams 时会调用
    override fun onAttachedToLayoutParams(params: CoordinatorLayout.LayoutParams) {
        super.onAttachedToLayoutParams(params)
    }


// LayoutParams 移除时会调用
    override fun onDetachedFromLayoutParams() {
        super.onDetachedFromLayoutParams()
    }


    // 是否拦截 CoordinatorLayout 发过了的点击事件

    override fun onInterceptTouchEvent(
            parent: CoordinatorLayout,
            child: View,
            ev: MotionEvent
    ): Boolean {
        return super.onInterceptTouchEvent(parent, child, ev)
    }

// 接收 CoordinatorLayout 发过了的点击事件

    override fun onTouchEvent(
            parent: CoordinatorLayout,
            child: View,
            ev: MotionEvent
    ): Boolean {
        Timber.tag("Title2Behavior").i("onTouchEvent:")
        return super.onTouchEvent(parent, child, ev)
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, type: Int) {
        Timber.tag("Title2Behavior").i("onNestedPreScroll:type:$type")
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
    }
}