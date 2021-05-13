package me.shetj.customviewdemo.recycle.edge

import android.view.View
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.chad.library.adapter.base.viewholder.BaseViewHolder


class EdgeViewHolder(view:View):BaseViewHolder(view) {
    val animY = SpringAnimation(this.itemView, SpringAnimation.TRANSLATION_Y)
        .setSpring(
            SpringForce()
                .setFinalPosition(0f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_LOW)
        )
}