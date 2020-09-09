package me.shetj.custom

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.sqrt


fun z(x:Int, y :Int): Int {
    return sqrt((x * x + y * y).toDouble()).toInt()
}


/**
 * 弧度换算成角度
 *
 * @return
 */
fun radianToDegree(radian: Double): Double {
    return radian * 180 / Math.PI
}

/**
 * 角度换算成弧度
 * @param degree
 * @return
 */
fun degreeToRadian(degree: Double): Double {
    return degree * Math.PI / 180
}

/**
 * 两个点之间的距离
 * @param pf1
 * @param pf2
 * @return
 */
fun distance4PointF(pf1: PointF, pf2: PointF): Float {
    val disX = pf2.x - pf1.x
    val disY = pf2.y - pf1.y
    return sqrt(disX * disX + disY * disY.toDouble()).toFloat()
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
        Log.e("save",e.message.toString())
    }
    return false
}

// 通过uri获取bitmap
fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    var parcelFileDescriptor: ParcelFileDescriptor? = null
    val fileDescriptor: FileDescriptor?
    var bitmap: Bitmap? = null;
    try {
        parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r")
        if ((parcelFileDescriptor?.fileDescriptor) != null) {
            fileDescriptor = parcelFileDescriptor.fileDescriptor
            //转换uri为bitmap类型
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            parcelFileDescriptor?.close()
        } catch (e: IOException) {

        }
    }
    return bitmap
}