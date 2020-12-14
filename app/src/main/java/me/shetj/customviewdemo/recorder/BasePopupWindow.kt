package me.shetj.customviewdemo.recorder

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewbinding.ViewBinding
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.cancel
import me.shetj.base.R
import me.shetj.base.ktx.getClazz
import me.shetj.base.weight.AbLoadingDialog

/**
 * popup 弹窗
 * 设置点击窗口外边窗口不消失，activity 可以被点击
 * {
 *   isOutsideTouchable = false
 *   isFocusable = false
 * }
 */
abstract class BasePopupWindow<VB : ViewBinding>(private val mContext: AppCompatActivity) :
    PopupWindow(mContext), LifecycleObserver {

    private val lazyComposite = lazy { CompositeDisposable() }
    protected val mCompositeDisposable: CompositeDisposable by lazyComposite
    private val lazyViewBinding = lazy { initViewBinding() }
    protected val mViewBinding: VB by lazyViewBinding

    private val lazyScope = lazy { AbLoadingDialog.LoadingScope() }
    protected val coroutineScope: AbLoadingDialog.LoadingScope by lazyScope

    init {
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isOutsideTouchable = false// 设置点击窗口外边窗口不消失
        isFocusable = false
        contentView = mViewBinding.root
        mViewBinding.initUI()
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mContext.lifecycle.addObserver(this)
        setOnDismissListener {

        }
    }

    private fun initViewBinding(): VB {
        return getClazz<VB>(this, 0).getMethod("inflate", LayoutInflater::class.java)
            .invoke(null, mContext.layoutInflater) as VB
    }

    abstract fun VB.initUI()

    abstract fun showPop()

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun dismissStop() {
        try {
            if (lazyComposite.isInitialized()) {
                mCompositeDisposable.clear()
            }
            if (lazyScope.isInitialized()){
                coroutineScope.cancel()
            }
            dismiss()
        } catch (ignored: Exception) {
            //暴力解决，可能的崩溃
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun dismissOnDestroy() {
        try {
            if (lazyComposite.isInitialized()) {
                mCompositeDisposable.clear()
            }
            if (lazyScope.isInitialized()){
                coroutineScope.cancel()
            }
            dismiss()
        } catch (_: Exception) {
            //暴力解决，可能的崩溃
        }
    }
}