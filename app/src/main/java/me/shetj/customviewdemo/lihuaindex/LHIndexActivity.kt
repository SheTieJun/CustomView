package me.shetj.customviewdemo.lihuaindex

import androidx.fragment.app.Fragment
import me.shetj.base.mvvm.BaseBindingActivity
import me.shetj.base.tools.app.ArmsUtils.Companion.statuInScreen
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.databinding.ActivityLHIndexBinding

class LHIndexActivity : BaseBindingActivity<IndexViewModel, ActivityLHIndexBinding>() {

    override fun initViewBinding(): ActivityLHIndexBinding {
        return ActivityLHIndexBinding.inflate(layoutInflater)
    }

    override fun onActivityCreate() {
        super.onActivityCreate()
        statuInScreen(true)
        viewBindData()
    }

    private fun viewBindData() {
        mViewBinding.BottomNavigationView.apply {
            setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.navigation_class -> {
                        mViewBinding.viewPagerRoot.currentItem = 0
                    }
                    R.id.navigation_learn -> {
                        mViewBinding.viewPagerRoot.currentItem = 1
                    }
                    R.id.navigation_user -> {
                        mViewBinding.viewPagerRoot.currentItem = 2
                    }
                }
                return@setOnNavigationItemSelectedListener true
            }
        }

        mViewBinding.viewPagerRoot.apply {
            isUserInputEnabled = false
        }.adapter = IndexPagerAdapter(this,ArrayList<Fragment>().apply {
            add(IndexFragment())
            add(LearnFragment())
            add(UserFragment())
        })


    }
}