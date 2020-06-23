package me.shetj.custom.floatview

import android.view.View

/**
 * Created by yhao on 17-11-14.
 * https://github.com/yhaolpz
 */
internal abstract class FloatView {
    abstract fun setSize(width: Int, height: Int)
    abstract fun setView(view: View?)
    abstract fun setGravity(gravity: Int, xOffset: Int, yOffset: Int)
    abstract fun init()
    abstract fun dismiss()
    open fun updateXY(x: Int, y: Int) {}
    open fun updateX(x: Int) {}
    open fun updateY(y: Int) {}
    open val x: Int
        get() = 0

    open val y: Int
        get() = 0

    open fun updateWH(width: Int, height: Int) {}
}