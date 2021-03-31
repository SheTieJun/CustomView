package me.shetj.choice

import androidx.recyclerview.widget.LinearLayoutManager
import me.shetj.base.mvvm.BaseBindingActivity
import me.shetj.base.mvvm.BaseViewModel
import me.shetj.choice.adapter.node.NodeAdapter
import me.shetj.choice.databinding.ActivityMainBinding
import me.shetj.choice.databinding.ActivityNodeBinding
import me.shetj.choice.model.node.SemesterProvider

class NodeActivity : BaseBindingActivity<BaseViewModel, ActivityNodeBinding>() {

    private val provider : SemesterProvider by lazy { SemesterProvider() }
    private val mAdapter : NodeAdapter = NodeAdapter()

    override fun onActivityCreate() {
        super.onActivityCreate()
        mViewBinding.recycleView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@NodeActivity)
        }

        mViewBinding.SwipeRefreshLayout.setOnRefreshListener {
            provider.init()
        }

        provider.semesterList.observe(this){
            mViewBinding.SwipeRefreshLayout.isRefreshing = false
            mAdapter.setNewInstance(it)
        }


        provider.init()
    }


}