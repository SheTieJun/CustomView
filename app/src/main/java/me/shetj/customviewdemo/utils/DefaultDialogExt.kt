package me.shetj.customviewdemo.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.viewbinding.ViewBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.coroutines.delay
import me.shetj.base.ktx.getClazz
import me.shetj.base.ktx.logi
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.customviewdemo.R
import java.util.concurrent.TimeUnit


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
            window?.setLayout(ArmsUtils.dp2px(300f), LinearLayout.LayoutParams.WRAP_CONTENT);
        }
}


fun <VB:ViewBinding> createDialog(
    context: Context,
    mVB:VB,
    viewListener: ((mVB: VB) -> Unit)? = null
): AlertDialog? {
    viewListener?.invoke(mVB)
    return AlertDialog.Builder(context)
        .setView(mVB.root)
        .show()?.apply {
            window?.setLayout(ArmsUtils.dp2px(300f), LinearLayout.LayoutParams.WRAP_CONTENT);
        }
}


inline fun <reified VB : ViewBinding> Context.createSimDialog(
    crossinline viewListener: ((mVB: VB) -> Unit) = { },
    crossinline setWindowSizeChange: ((win: Window?) -> Unit) = {
        it?.setLayout(ArmsUtils.dp2px(300f), LinearLayout.LayoutParams.WRAP_CONTENT)
    }
): AlertDialog? {
    val mVB = VB::class.java.getMethod("inflate", LayoutInflater::class.java)
        .invoke(null, LayoutInflater.from(this)) as VB
    viewListener.invoke(mVB)
    return AlertDialog.Builder(this)
        .setView(mVB.root)
        .show()?.apply {
            setWindowSizeChange.invoke(window)
        }
}

fun showStickyViewDialog(context: Context) {
    createDialog(context, R.layout.layou_sticky_view) {

    }
}


fun createLoading(context: Context) {
    LoadingDialog.showWithAction(context) {
        "开始".logi()
        delay(5000)
        "结束".logi()
    }.apply {
        AndroidSchedulers.mainThread().scheduleDirect({
                hideLoading()
            }, 5100, TimeUnit.MILLISECONDS)
    }
}