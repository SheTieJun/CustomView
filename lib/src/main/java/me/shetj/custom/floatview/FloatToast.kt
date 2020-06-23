package me.shetj.custom.floatview

import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import java.lang.reflect.Method

/**
 * 自定义 toast 方式，无需申请权限
 */
internal class FloatToast(applicationContext: Context?) : FloatView() {
    private val toast: Toast = Toast(applicationContext)
    private var mTN: Any? = null
    private var show: Method? = null
    private var hide: Method? = null
    private var mWidth = 0
    private var mHeight = 0
    public override fun setSize(width: Int, height: Int) {
        mWidth = width
        mHeight = height
    }

    public override fun setView(view: View?) {
        toast.view = view
        initTN()
    }

    public override fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
        toast.setGravity(gravity, xOffset, yOffset)
    }

    public override fun init() {
        try {
            show!!.invoke(mTN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public override fun dismiss() {
        try {
            hide!!.invoke(mTN)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initTN() {
        try {
            //todo android P test
            val tnField = toast.javaClass.getDeclaredField("mTN")
            tnField.isAccessible = true
            mTN = tnField[toast]
            show = mTN?.javaClass!!.getMethod("show")
            hide = mTN?.javaClass!!.getMethod("hide")
            val tnParamsField = mTN?.javaClass!!.getDeclaredField("mParams")
            tnParamsField.isAccessible = true
            val params =
                tnParamsField[mTN] as WindowManager.LayoutParams
            params.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            params.width = mWidth
            params.height = mHeight
            params.windowAnimations = 0
            val tnNextViewField =
                mTN?.javaClass!!.getDeclaredField("mNextView")
            tnNextViewField.isAccessible = true
            tnNextViewField[mTN] = toast.view
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public override fun updateWH(width: Int, height: Int) {
        mWidth = width
        mHeight = height
        initTN()
    }

}