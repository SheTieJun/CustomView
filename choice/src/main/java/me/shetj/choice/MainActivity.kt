package me.shetj.choice

import androidx.recyclerview.widget.LinearLayoutManager
import me.shetj.base.ktx.start
import me.shetj.base.mvvm.BaseBindingActivity
import me.shetj.base.mvvm.BaseViewModel
import me.shetj.choice.adapter.SemesterAdapter
import me.shetj.choice.databinding.ActivityMainBinding
import me.shetj.choice.model.SemesterProvider

class MainActivity : BaseBindingActivity<BaseViewModel,ActivityMainBinding>() {

    private val provider :SemesterProvider by lazy { SemesterProvider() }
    private val mAdapter :SemesterAdapter = SemesterAdapter()

    override fun onActivityCreate() {
        super.onActivityCreate()
        mViewBinding.recycleView.apply {
            adapter = mAdapter.apply {
                setOnItemClickListener { adapter, view, position ->
                    mAdapter.changeShowItem(position)
                }
            }
            layoutManager = LinearLayoutManager(this@MainActivity)

        }

        mViewBinding.SwipeRefreshLayout.setOnRefreshListener {
            provider.init()
        }

        provider.semesterList.observe(this){
            mViewBinding.SwipeRefreshLayout.isRefreshing = false
            mAdapter.setNewInstance(it)
        }


        mViewBinding.node.setOnClickListener {
            start<NodeActivity>()
        }
        provider.init()
    }


}