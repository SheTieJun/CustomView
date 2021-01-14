package me.shetj.customviewdemo.tx.video

import com.google.android.material.appbar.AppBarLayout


fun AppBarLayout.enableAppBar(enable: Boolean) {
    val mAppBarChildAt = getChildAt(0);
    val mAppBarParams = (mAppBarChildAt.layoutParams as AppBarLayout.LayoutParams)
    if (enable) {
        mAppBarParams.scrollFlags =
            AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
    } else {
        mAppBarParams.scrollFlags = 0
    }
    mAppBarChildAt.layoutParams = mAppBarParams;
}