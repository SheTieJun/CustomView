package me.shetj.custom.floatview

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi

/**
 * Created by yhao on 2017/12/22.
 * https://github.com/yhaolpz
 */
object Util {
    @JvmStatic
    fun inflate(applicationContext: Context, layoutId: Int): View {
        val inflate =
            applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return inflate.inflate(layoutId, null)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun hasPermission(context: Context?): Boolean {
        return Settings.canDrawOverlays(context)
    }

    private var sPoint: Point? = null
    @JvmStatic
    fun getScreenWidth(context: Context): Int {
        if (sPoint == null) {
            sPoint = Point()
            val wm =
                context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getSize(sPoint)
        }
        return sPoint!!.x
    }

    @JvmStatic
    fun getScreenHeight(context: Context): Int {
        if (sPoint == null) {
            sPoint = Point()
            val wm =
                context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getSize(sPoint)
        }
        return sPoint!!.y
    }
}