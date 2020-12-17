package me.shetj.customviewdemo.utils

import androidx.annotation.Keep
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 主要是计算周
 */
@Keep
object DateUtils2 {
    private var mYear // 当前年  
            : String? = null
    private var mMonth // 月  
            : String? = null
    private var mDay: String? = null
    private var mWay: String? = null// 获取当前月份  

    /**
     * 获取当前年月日
     */
    fun StringData(): String {
        val c = Calendar.getInstance()
        c.timeZone = TimeZone.getTimeZone("GMT+8:00")
        mYear = c[Calendar.YEAR].toString() // 获取当前年份  
        mMonth = (c[Calendar.MONTH] + 1).toString() // 获取当前月份  
        mDay = c[Calendar.DAY_OF_MONTH].toString() // 获取当前月份的日期号码  
        return "$mYear-$mMonth-$mDay"
    }

    fun getWeek(time: String?): String {
        var Week = ""
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val c = Calendar.getInstance()
        try {
            c.time = format.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (c[Calendar.DAY_OF_WEEK] == Calendar.SUNDAY) {
            Week += "周日"
        }
        if (c[Calendar.DAY_OF_WEEK] == Calendar.MONDAY) {
            Week += "周一"
        }
        if (c[Calendar.DAY_OF_WEEK] == Calendar.TUESDAY) {
            Week += "周二"
        }
        if (c[Calendar.DAY_OF_WEEK] == Calendar.WEDNESDAY) {
            Week += "周三"
        }
        if (c[Calendar.DAY_OF_WEEK] == Calendar.THURSDAY) {
            Week += "周四"
        }
        if (c[Calendar.DAY_OF_WEEK] == Calendar.FRIDAY) {
            Week += "周五"
        }
        if (c[Calendar.DAY_OF_WEEK] == Calendar.SATURDAY) {
            Week += "周六"
        }
        return Week
    }

    fun get7date(): List<String> {
        val dates: MutableList<String> = ArrayList()
        val c = Calendar.getInstance()
        c.timeZone = TimeZone.getTimeZone("GMT+8:00")
        val sim = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var date = sim.format(c.time)
        dates.add(date)
        for (i in 0..5) {
            c.add(Calendar.DAY_OF_MONTH, 1)
            date = sim.format(c.time)
            dates.add(date)
        }
        return dates
    }// 获取当前年份

    // 获取当前月份

    //获取当前日份的日期号码
    /**
     * 获取今天往后一周的日期（几月几号）
     */
    val sevenDate: List<String>
        get() {
            val dates: MutableList<String> = ArrayList()
            val c = Calendar.getInstance()
            c.timeZone = TimeZone.getTimeZone("GMT+8:00")
            for (i in 0..6) {
                // 获取当前年份
                mYear = c[Calendar.YEAR].toString()

                // 获取当前月份
                mMonth = (c[Calendar.MONTH] + 1).toString()

                //获取当前日份的日期号码
                mDay = (c[Calendar.DAY_OF_MONTH] + i).toString()
                val date = mMonth + "月" + mDay + "日"
                dates.add(date)
            }
            return dates
        }

    fun get7dateT(): List<String> {
        val dates: MutableList<String> = ArrayList()
        val c = Calendar.getInstance()
        c.timeZone = TimeZone.getTimeZone("GMT+8:00")
        for (i in 0..6) {
            mYear = c[Calendar.YEAR].toString()
            // 获取当前年份
            mMonth = (c[Calendar.MONTH] + 1).toString()
            // 获取当前月份
            c.add(Calendar.DAY_OF_MONTH, 1)
            mDay = c[Calendar.DAY_OF_MONTH].toString()
            // 获取当前日份的日期号码
            val date = mDay + ""
            dates.add(date)
        }
        return dates
    }

    /**
     * 获取今天往后一周的集合
     */
    fun get7week(): List<String> {
        var week = ""
        val weeksList: MutableList<String> = ArrayList()
        val dateList = get7date()
        for (s in dateList) {
            week = if (s == StringData()) {
                "今天"
            } else {
                getWeek(s)
            }
            weeksList.add(week)
        }
        return weeksList
    }


    fun get7dateAndToday(): List<String> {
        val dates: MutableList<String> = ArrayList()
        val c = Calendar.getInstance()
        c.timeZone = TimeZone.getTimeZone("GMT+8:00")
        for (i in 0..6) {
            if (i != 0) {
                c.add(Calendar.DAY_OF_MONTH, 1)
            }
            val date = c[Calendar.DAY_OF_MONTH].toString()
            dates.add(date)
        }
        return dates
    }
}