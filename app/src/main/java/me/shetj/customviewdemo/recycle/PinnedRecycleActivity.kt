package me.shetj.customviewdemo.recycle

import androidx.recyclerview.widget.LinearLayoutManager
import com.oushangfeng.pinnedsectionitemdecoration.PinnedHeaderItemDecoration
import me.shetj.base.mvvm.BaseBindingActivity
import me.shetj.base.mvvm.BaseViewModel
import me.shetj.customviewdemo.databinding.ActivityPinnedRecycleBinding


class PinnedRecycleActivity : BaseBindingActivity<BaseViewModel, ActivityPinnedRecycleBinding>() {
    override fun initViewBinding(): ActivityPinnedRecycleBinding {
        return ActivityPinnedRecycleBinding.inflate(layoutInflater)
    }

    override fun onActivityCreate() {
        super.onActivityCreate()
        mViewBinding.iRecyclerView.apply {
            adapter = TestAdapter(ArrayList<MultiItem>().apply {
                repeat(21) {
                    if (it == 0) {
                        add(MultiItem(1))
                    } else {
                        add(MultiItem(2))
                    }
                }
            })
            layoutManager = LinearLayoutManager(this@PinnedRecycleActivity)
            addItemDecoration(
                PinnedHeaderItemDecoration.Builder(1)
                    .create()
            )
        }
    }

}