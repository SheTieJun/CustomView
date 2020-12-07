package me.shetj.customviewdemo

import android.content.Context
import android.content.res.AssetManager
import android.text.format.Formatter
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader



fun getNextLevelData(level: Int):Int{
    return when(level +1){
        1 -> 1
        2 -> 3
        3 -> 9
        4 -> 27
        5 -> 81
        6 -> 243
        7 -> 729
        8 -> 2189
        else -> 2189*3
    }
}

/**
 * 矩阵转换数据
 */
fun <T> transform(dataList: List<T?>,row:Int ,column:Int): List<T?> {
    val destList: MutableList<T?> = ArrayList()
    //页数
    val pageSize: Int = row * column
    //总数量
    val size = dataList.size
    //总换后的总数量，包括一页空的数据
    val afterTransformSize: Int
    afterTransformSize = if (size < pageSize) {
        pageSize
    } else if (size % pageSize == 0) {
        size
    } else {
        (size / pageSize + 1) * pageSize
    }
    //开始遍历位置，类似置换矩阵
    for (i in 0 until afterTransformSize) {
        //第几页
        val pageIndex = i / pageSize
        //为横坐标
        val columnIndex: Int = (i - pageSize * pageIndex) / row
        //为纵坐标
        val rowIndex: Int = (i - pageSize * pageIndex) % row
        //
        val result: Int = rowIndex * column + columnIndex + pageIndex * pageSize
        if (result in 0 until size) {
            destList.add(dataList[result])
        } else {
            destList.add(null)
        }
    }
    return destList
}

object MemoryUtils{
    @JvmStatic
    val availableMemInBytes: Long
        get() {
            val runtime = Runtime.getRuntime()
            val usedMem = runtime.totalMemory() - runtime.freeMemory()
            val maxHeapSize = runtime.maxMemory()
            return maxHeapSize - usedMem
        }

    @JvmStatic
    fun getHeapMemStats(context: Context): String {
        val maxMemInBytes = Runtime.getRuntime().maxMemory()
        val availableMemInBytes = availableMemInBytes
        val usedMemInBytes = maxMemInBytes - availableMemInBytes
        val usedMemInPercentage = usedMemInBytes * 100 / maxMemInBytes
        return "used: " + Formatter.formatShortFileSize(context, usedMemInBytes) + " / " +
                Formatter.formatShortFileSize(context, maxMemInBytes) + " (" + usedMemInPercentage + "%)"
    }
}
