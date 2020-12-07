package me.shetj.customviewdemo.tansition

import android.animation.Animator
import android.content.Context
import android.transition.Scene
import android.transition.Scene.getSceneForLayout
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionManager
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.transition.doOnEnd
import androidx.core.transition.doOnStart
import androidx.core.view.isVisible
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.utils.createDialog


fun showDialogLogin(context: Context) {
    createDialog(context, R.layout.layou_login_and_register) { root ->
        val root: ConstraintLayout = root.findViewById(R.id.root)
        val loginScene = getSceneForLayout(root, R.layout.layout_login, context)
        val registerScene = getSceneForLayout(root, R.layout.layout_register, context)
        goLogin(context, loginScene, root, registerScene)

    }
}

private fun goLogin(
    context: Context,
    loginScene: Scene,
    root: ConstraintLayout,
    registerScene: Scene
) {
    TransitionManager.go(loginScene, context.getTransition(R.transition.go_login_triansition)?.apply {
        doOnStart {
            val card = root.findViewById<View>(R.id.card)
            getExpansionAnimation(card,true).start()
        }
    })
    root.findViewById<FloatingActionButton>(R.id.btn_register).apply {
        setOnClickListener {
            val card = root.findViewById<View>(R.id.card)
            getGoLoginAnimation(card, true).apply {
                doOnEnd {
                    goRegister(context, registerScene, root, loginScene)
                }
            }.start()
        }
    }
}

private fun goRegister(
    context: Context,
    registerScene: Scene,
    root: ConstraintLayout,
    loginScene: Scene
) {

    TransitionManager.go(
        registerScene,
        context.getTransition(R.transition.go_register_triansition).apply {
            //移动成功后展开视图
            this?.doOnEnd {
                val card = root.findViewById<View>(R.id.card)
                getExpansionAnimation(card, false).start()
            }
        })
    root.findViewById<FloatingActionButton>(R.id.btn_register).apply {
        setOnClickListener {
            val card = root.findViewById<View>(R.id.card)
            getGoLoginAnimation(card,false).apply {
                this.doOnEnd {
                    goLogin(context, loginScene, root, registerScene)
                }
            }.start()
        }
    }
}

//展开
private fun getExpansionAnimation(card: View, isLogin: Boolean = false): Animator {
    card.isVisible = true
    return ViewAnimationUtils.createCircularReveal(
        card,
        when(isLogin) {
            true -> card.width
            false -> card.width / 2
        },
        when(isLogin) {
            true -> ArmsUtils.dp2px(50f)
            false -> 0
        },
        0f,
        card.height.toFloat()
    ).apply {
        duration = 500
        interpolator = AccelerateInterpolator()
    }
}

//收起
private fun getGoLoginAnimation(card: View, isLogin: Boolean = false): Animator {
    return ViewAnimationUtils.createCircularReveal(
        card,
        when(isLogin) {
            true -> card.width
            false -> card.width / 2
        },
        when(isLogin) {
            true -> ArmsUtils.dp2px(50f)
            false -> 0
        },
        card.height.toFloat(),
        0f
    ).apply {
        duration = 500
        interpolator = AccelerateInterpolator()
    }
}

fun Context.getTransition(resource: Int): Transition? {
    return TransitionInflater.from(this).inflateTransition(resource)
}