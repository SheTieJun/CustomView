package me.shetj.customviewdemo.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import androidx.annotation.Keep
import androidx.annotation.LayoutRes
import me.shetj.customviewdemo.R
import timber.log.Timber


@Keep
object MedalDialog {

    private var mLoadingDialog: Dialog? = null

    fun showDialog(context: Activity, cancelable: Boolean, @LayoutRes  resource :Int,
                   showListener:((v: View) ->Unit)? =null): Dialog {
        if (null != mLoadingDialog) {
            mLoadingDialog!!.cancel()
        }
        val view = LayoutInflater.from(context).inflate(resource, null)
        mLoadingDialog = Dialog(context, R.style.CustomProgressDialog)
        mLoadingDialog!!.setCancelable(cancelable)
        mLoadingDialog!!.setCanceledOnTouchOutside(false)
        mLoadingDialog!!.setContentView(view, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))
        val ivMedal = view.findViewById<View>(R.id.iv_medal)
        val otherView = view.findViewById<View>(R.id.other_view)
        val medalTop = view.findViewById<View>(R.id.iv_medal_top)
        val bgMedal = view.findViewById<View>(R.id.iv_medal_bg)
        otherView.alpha = 0f
        medalTop.alpha = 0f
        ivMedal.scaleX = 0.2f
        ivMedal.scaleY = 0.2f
        mLoadingDialog!!.setOnShowListener {
            showListener?.invoke(view)
            Timber.i("setOnShowListener")
            ivMedal.animate().scaleX(1.25f)
                .scaleY(1.25f).apply {
                    if (!Build.MANUFACTURER.contains("HUAWEI", true)) {
                        rotationYBy(720f)
                    }else{
                        rotation(720f)
                    }
                }
                .withEndAction {
                    ivMedal.animate().scaleX(1.0f)
                        .scaleY(1.0f)
                        .withStartAction {
                            otherView.animate().alpha(1f).setDuration(800).start()
                            medalTop.animate().alpha(1f).setDuration(800).start()
                            bgMedal.ration()
                        }
                        .start()
                }
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setDuration(1500)
                .start()
        }
        mLoadingDialog!!.setOnDismissListener {
            bgMedal.clearAnimation()
        }
        view.setOnClickListener { mLoadingDialog!!.dismiss() }
        mLoadingDialog!!.show()
        return mLoadingDialog as Dialog
    }

    fun hideLoading() {
        if (null != mLoadingDialog) {
            mLoadingDialog!!.cancel()
        }
    }

    private fun View.ration(){
        val objectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 359f);
        objectAnimator.repeatCount = ValueAnimator.INFINITE;
        objectAnimator.duration = 30000
        objectAnimator.interpolator = LinearInterpolator();
        objectAnimator.start();
    }

}
