package me.shetj.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Rect
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

// 通过uri获取bitmap
fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    var parcelFileDescriptor: ParcelFileDescriptor? = null
    var fileDescriptor: FileDescriptor? = null;
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