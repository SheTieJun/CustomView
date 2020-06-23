package me.shetj.custom.floatview

import android.view.View
import me.shetj.custom.floatview.Screen.screenType

/**
 * Created by yhao on 2017/12/22.
 * https://github.com/yhaolpz
 */
abstract class IFloatWindow {
    abstract fun show()
    abstract fun hide()
    abstract val x: Int
    abstract val y: Int
    abstract fun updateX(x: Int)
    abstract fun updateX(@screenType screenType: Int, ratio: Float)
    abstract fun updateY(y: Int)
    abstract fun updateY(@screenType screenType: Int, ratio: Float)
    abstract fun updateWh(width: Int, height: Int)
    abstract val view: View?
    abstract fun dismiss()
}