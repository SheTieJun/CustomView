package me.shetj.customviewdemo.lihuaindex

import androidx.core.content.ContextCompat
import me.shetj.base.mvvm.BaseBindingActivity
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.databinding.ActivityLHIndexBinding

class LHIndexActivity : BaseBindingActivity<IndexViewModel,ActivityLHIndexBinding>() {

    override fun initViewBinding(): ActivityLHIndexBinding {
        return ActivityLHIndexBinding.inflate(layoutInflater)
    }

    override fun onActivityCreate() {
        super.onActivityCreate()
    
        mViewBinding.slidingTabLayout.apply {
             isTabSpaceEqual = false
             tabPadding = 15f
             indicatorWidth = 22f
             setIndicatorMargin(0f,1f,0f,2f)
             textsize = 14f
             textBold = 1
             textSelectColor = ContextCompat.getColor(context, R.color.blackText)
             indicatorColor = ContextCompat.getColor(context, R.color.blackDivider)
             textUnselectColor = ContextCompat.getColor(context, R.color.blackSecondText)
        }.setViewPager(mViewBinding.viewPager, arrayOf("全部课程","接单市集"),this,
            arrayListOf(BlankFragment.newInstance("1","2"),BlankFragment.newInstance("1","2")))
    }
}