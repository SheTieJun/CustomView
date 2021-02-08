package me.shetj.customviewdemo.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import me.shetj.base.ktx.logi
import timber.log.Timber


class TitleBehavior  @JvmOverloads constructor(context: Context?, attrs: AttributeSet? =null):CoordinatorLayout.Behavior<View>() {



    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        "onDependentViewChanged".logi()

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
        "layoutDependsOn:view".logi()
        return true
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
        "onStartNestedScroll".logi()
        return false
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
        "onNestedPreScroll".logi()
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
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
        Timber.tag("TitleBehavior").i("onNestedPreScroll: \n " +
                "               dxConsumed:$dxConsumed,\n" +
                "                dyConsumed:$dyConsumed,\n" +
                "                dxUnconsumed:$dxUnconsumed,\n" +
                "                dyUnconsumed:$dyUnconsumed,\n" +
                "                type:$type,")
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

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: View, target: View, type: Int) {
        Timber.tag("TitleBehavior").i("onStopNestedScroll:  type:$type,")
        super.onStopNestedScroll(coordinatorLayout, child, target, type)

    }
}