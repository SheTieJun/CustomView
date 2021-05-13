package me.shetj.customviewdemo.recycle.edge

import android.graphics.Canvas
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.EdgeEffectFactory.DIRECTION_BOTTOM
import androidx.recyclerview.widget.RecyclerView.EdgeEffectFactory.DIRECTION_RIGHT
import me.shetj.base.ktx.dp2px
import me.shetj.base.ktx.logi


class SpringEdgeEffect(private val recyclerView: RecyclerView, val direction: Int) :
    AbEdgeEffect(recyclerView.context) {
    val level = if (direction == DIRECTION_BOTTOM || direction == DIRECTION_RIGHT) {
        -1
    } else {
        1
    }

    override fun onPull(deltaDistance: Float) {
        super.onPull(deltaDistance)
    }

    override fun onPull(deltaDistance: Float, displacement: Float) {
        super.onPull(deltaDistance, displacement)
        "deltaDistance =$deltaDistance | displacement = $displacement".logi()
        recyclerView.addTranY(deltaDistance)
    }


    private fun RecyclerView.addTranY(deltaDistance: Float) {
        val translationYDelta =
            level * recyclerView.height * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
        findEachViewHolder<EdgeViewHolder> {
            this?.let {
                "deltaDistance =$deltaDistance:${this.layoutPosition}:translationYDelta=$translationYDelta".logi()
                this.itemView.translationY += translationYDelta
                animY?.cancel()
            }
        }
    }

    private fun RecyclerView.addEdgeAnim(velocity: Int) {
        val translationVelocity = level * velocity * FLING_TRANSLATION_MAGNITUDE
        findEachViewHolder<EdgeViewHolder> {
            this?.apply {
                animY.setStartVelocity(translationVelocity)
                    .start()
            }
        }
    }


    override fun onRelease() {
        super.onRelease()
        "onRelease ".logi()
        recyclerView.addEdgeAnim(20f.dp2px)
    }

    override fun onAbsorb(velocity: Int) {
        super.onAbsorb(velocity)
        "onAbsorb = $velocity".logi()
        recyclerView.addEdgeAnim(velocity)
    }


    companion object {
        private const val OVERSCROLL_TRANSLATION_MAGNITUDE = 0.2f

        private const val FLING_TRANSLATION_MAGNITUDE = 0.5f
    }
}