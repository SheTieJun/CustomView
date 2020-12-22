package com.tencent.liteav.superplayer

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

inline fun View?.animateToHeight(crossinline postAction: () -> Unit = {}) {
    if (this == null) {
        return
    }

    val actualTarget = ((parent as View).height).toInt()

    val layoutParams = ConstraintLayout.LayoutParams(this.layoutParams)

    this.layoutParams = layoutParams

    ObjectAnimator.ofInt(this.height, actualTarget).also {
        it.interpolator = FastOutSlowInInterpolator()
        it.addUpdateListener { animator ->
            this.layoutParams.also { lp ->
                lp.height = animator.animatedValue as Int
                requestLayout()
            }
        }
        it.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                postAction.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        })
        it.start()
    }
}