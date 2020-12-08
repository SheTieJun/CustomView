package me.shetj.customviewdemo.lihuaindex

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class IndexPagerAdapter : FragmentStateAdapter {
    private lateinit var lists:ArrayList<Fragment>
    constructor(fragmentActivity: FragmentActivity,lists:ArrayList<Fragment>) : super(fragmentActivity) {
        this.lists = lists
    }
    constructor(fragment: Fragment,lists:ArrayList<Fragment>) : super(fragment) {
        this.lists = lists
    }

    override fun createFragment(position: Int): Fragment {
        return lists[position]
    }

    override fun getItemCount(): Int {
        return lists.size
    }


}