package me.shetj.customviewdemo.recycle

import android.widget.EdgeEffect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.shetj.base.mvvm.BaseBindingActivity
import me.shetj.base.mvvm.BaseViewModel
import me.shetj.customviewdemo.databinding.ActivityPinnedRecycleBinding
import me.shetj.customviewdemo.recycle.edge.SpringEdgeEffect


class PinnedRecycleActivity : BaseBindingActivity<BaseViewModel, ActivityPinnedRecycleBinding>() {
    override fun initViewBinding(): ActivityPinnedRecycleBinding {
        return ActivityPinnedRecycleBinding.inflate(layoutInflater)
    }

    override fun onActivityCreate() {
        super.onActivityCreate()
        mViewBinding.iRecyclerView.apply {
            adapter = TestAdapter(ArrayList<MultiItem>().apply {
                repeat(21) {
                    add(MultiItem(2))
                }
            })
            layoutManager = LinearLayoutManager(this@PinnedRecycleActivity)
            edgeEffectFactory = object :RecyclerView.EdgeEffectFactory(){
                override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
                    return SpringEdgeEffect(view,direction)
                }
            }
        }
    }

}