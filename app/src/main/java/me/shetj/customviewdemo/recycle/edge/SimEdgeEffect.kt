package me.shetj.customviewdemo.recycle.edge

import android.content.Context


class SimEdgeEffect(context: Context) :AbEdgeEffect(context) {

    override fun onPull(deltaDistance: Float) {
        super.onPull(deltaDistance)
    }

    override fun onPull(deltaDistance: Float, displacement: Float) {
        super.onPull(deltaDistance, displacement)
    }

    override fun onRelease() {
        super.onRelease()
    }

    override fun onAbsorb(velocity: Int) {
        super.onAbsorb(velocity)
    }
}