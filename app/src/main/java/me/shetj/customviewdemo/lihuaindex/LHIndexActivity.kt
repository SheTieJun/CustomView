package me.shetj.customviewdemo.lihuaindex

import androidx.fragment.app.Fragment
import me.shetj.base.mvvm.BaseBindingActivity
import me.shetj.base.tools.app.ArmsUtils.Companion.statuInScreen
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.databinding.ActivityLHIndexBinding

class LHIndexActivity : BaseBindingActivity<IndexViewModel, ActivityLHIndexBinding>() {

    private var currentFragment: Fragment ? = null
    private var classFragment:Fragment ?= null
    private var userCenterFragment:Fragment ?= null
    private var startLearningFragment:Fragment ?= null

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

//    private fun showClassFragment() {
//        if (currentFragment === classFragment && currentFragment != null) return
//        val ft = supportFragmentManager.beginTransaction()
//        if (currentFragment != null) ft.hide(currentFragment!!)
//        if (classFragment == null) {
//            classFragment =
//                supportFragmentManager.findFragmentByTag("classFragment")
//        }
//        if (classFragment == null) {
//            classFragment = IndexFragment()
//            ft.add(mViewBinding.viewPagerRoot.id,
//                classFragment!!,
//                "classFragment"
//            )
//        } else {
//            ft.show(classFragment!!)
//        }
//        ft.commitAllowingStateLoss()
//        currentFragment = classFragment
//
//    }
//
//    private fun showUserFragment() {
//        if (currentFragment === userCenterFragment && currentFragment != null) return
//        val ft = supportFragmentManager.beginTransaction()
//        if (currentFragment != null) ft.hide(currentFragment!!)
//        if (userCenterFragment == null) {
//            userCenterFragment =
//                supportFragmentManager.findFragmentByTag("userCenterFragment")
//        }
//        if (userCenterFragment == null) {
//            userCenterFragment = UserFragment()
//            ft.add(mViewBinding.viewPagerRoot.id,
//                userCenterFragment!!,
//                "userCenterFragment"
//            )
//        } else {
//            ft.show(userCenterFragment!!)
//        }
//        ft.commitAllowingStateLoss()
//        currentFragment = userCenterFragment
//
//    }
//
//
//    private fun  showLearnFragment() {
//        if (currentFragment === startLearningFragment && currentFragment != null) return
//        val ft = supportFragmentManager.beginTransaction()
//        if (currentFragment != null) ft.hide(currentFragment!!)
//        if (startLearningFragment == null) {
//            startLearningFragment =
//                supportFragmentManager.findFragmentByTag("startLearningFragment")
//        }
//        if (startLearningFragment == null) {
//            startLearningFragment = LearnFragment()
//            ft.add(mViewBinding.viewPagerRoot.id,
//                startLearningFragment!!,
//                "startLearningFragment"
//            )
//        } else {
//            ft.show(startLearningFragment!!)
//        }
//        ft.commitAllowingStateLoss()
//        currentFragment = startLearningFragment
//    }
}