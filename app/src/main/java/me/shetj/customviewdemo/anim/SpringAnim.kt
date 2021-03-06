package me.shetj.customviewdemo.anim

import android.content.Context
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import me.shetj.customviewdemo.databinding.LayouSpringAnimBinding
import me.shetj.customviewdemo.utils.createSimDialog


object SpringAnim {

    fun  showSpringAnim(context: Context){
        context.createSimDialog<LayouSpringAnimBinding>(viewListener = {vb ->
            val springForce  = SpringForce(0f)
                .setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY)
                .setStiffness(SpringForce.STIFFNESS_VERY_LOW)
            val springAnim = SpringAnimation(vb.ivMove,DynamicAnimation.TRANSLATION_Y)
            springAnim.spring = springForce
            vb.btnStart.setOnClickListener {
                springAnim.cancel()
                springAnim.setStartVelocity(1500f)
                    .start()
            }
        })
    }
}