package me.shetj.custom

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.sqrt


fun z(x:Int, y :Int): Int {
    return sqrt((x * x + y * y).toDouble()).toInt()
}

/**
 * @return 返回一个Rect 他就是text 的容器宽高
 */
fun Paint.getTextWH(text:String,rect:Rect = Rect()): Rect {
    getTextBounds(text, 0, text.length, rect)
    return rect
}


fun Bitmap.saveImageBitmap(filename:String):Boolean{
    try {
        FileOutputStream(filename).use { out ->
            this.compress(
                Bitmap.CompressFormat.PNG,
                100,
                out
            )
        }
        return true
    } catch (e: IOException) {
        e.printStackTrace()
        Log.e("save",e.message)
    }
    return false
}