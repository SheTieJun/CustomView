package me.shetj.customviewdemo.behavior

import androidx.recyclerview.widget.LinearLayoutManager
import me.shetj.base.mvvm.BaseBindingActivity
import me.shetj.base.mvvm.BaseViewModel
import me.shetj.customviewdemo.databinding.ActivityBeHaviorBinding
import me.shetj.customviewdemo.tx.video.KeyListAdapter

class BeHaviorActivity : BaseBindingActivity<BaseViewModel,ActivityBeHaviorBinding>() {

    override fun onActivityCreate() {
        super.onActivityCreate()
        mViewBinding.iRecyclerView.adapter = KeyListAdapter(ArrayList<String>().apply {
            repeat(100){
                add(it.toString())
            }
        })
        mViewBinding.iRecyclerView.layoutManager = LinearLayoutManager(this)
    }

}