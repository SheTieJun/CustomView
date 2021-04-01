package me.shetj.choice

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import me.shetj.base.ktx.start
import me.shetj.base.mvvm.BaseBindingActivity
import me.shetj.base.mvvm.BaseViewModel
import me.shetj.choice.adapter.SemesterAdapter
import me.shetj.choice.databinding.ActivityMainBinding
import me.shetj.choice.model.Group
import me.shetj.choice.model.SemesterProvider
import me.shetj.choice.model.Student

class MainActivity : BaseBindingActivity<BaseViewModel,ActivityMainBinding>() {

    private val provider :SemesterProvider by lazy { SemesterProvider() }
    private val mAdapter :SemesterAdapter = SemesterAdapter()
    private var studentObserver: Observer<Pair<MutableList<Group>, MutableList<Student>>>?=null

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

        studentObserver = Observer<Pair<MutableList<Group>, MutableList<Student>>> {
            mViewBinding.tvMsg.text = "选择班级：${it.first.size}个，选择学生：${it.second.size}个"
        }
        SemesterProvider.dataChange.observe(this, studentObserver!!)
    }


    override fun onDestroy() {
        SemesterProvider.dataChange.removeObservers(this)
        studentObserver?.let { SemesterProvider.dataChange.removeObserver(it) }
        super.onDestroy()
    }

}

