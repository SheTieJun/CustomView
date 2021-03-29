package me.shetj.customviewdemo.utils

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import java.util.*


object CalendarKit {

    fun Context.addCalendar() {
        val startMillis: Long = System.currentTimeMillis()+60*1000
        val endMillis: Long = System.currentTimeMillis() + 60*5
        val tz = TimeZone.getDefault()
        val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                .putExtra(CalendarContract.Events.TITLE, "测试添加日历")
                .putExtra(CalendarContract.Events.DESCRIPTION, "测试添加日历")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, tz.displayName)
                .putExtra(CalendarContract.Events.CUSTOM_APP_PACKAGE, packageName)
                .putExtra(CalendarContract.Events.CUSTOM_APP_URI, "http://www.google.com")
        startActivity(intent)
    }
}