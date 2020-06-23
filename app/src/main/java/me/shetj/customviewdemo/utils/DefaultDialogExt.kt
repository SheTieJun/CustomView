package me.shetj.customviewdemo.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import me.shetj.base.tools.app.ArmsUtils


fun createDialog(
    context: Context,
    @LayoutRes layoutId: Int,
    viewListener: ((view: View) -> Unit)? = null
): AlertDialog? {
    val view = LayoutInflater.from(context).inflate(layoutId, null)
    viewListener?.invoke(view)
    return AlertDialog.Builder(context)
        .setView(view)
        .show()?.apply {
            window?.setLayout(ArmsUtils.dip2px(300f), LinearLayout.LayoutParams.WRAP_CONTENT);
        }
}