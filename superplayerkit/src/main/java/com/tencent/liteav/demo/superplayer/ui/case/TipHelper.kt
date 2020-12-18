package com.tencent.liteav.demo.superplayer.ui.case

import android.text.SpannableStringBuilder
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.tencent.liteav.demo.superplayer.R


class TipHelper (view:ViewGroup){

    private var tvMsg: TextView = view.findViewById(R.id.tv_tip)

    fun showMsg(msg: String) {
        tvMsg.visibility = View.VISIBLE
        tvMsg.text = msg
    }

    fun showMsg(result: SpannableStringBuilder) {
        tvMsg.visibility = View.VISIBLE
        tvMsg.text = result
    }

    fun showMsg(result: SpannableStringBuilder, hideAfter: Long) {
        var hideAfter = hideAfter
        tvMsg.visibility = View.VISIBLE
        tvMsg.text = result
        if (hideAfter <= 0) {
            hideAfter = 700
        }
        tvMsg.postDelayed({ tvMsg.visibility = View.GONE }, hideAfter)
    }


    fun showMsg(msg: String, delay: Long) {
        var delay = delay
        tvMsg.visibility = View.VISIBLE
        tvMsg.text = msg
        if (delay < 0) delay = 700
        tvMsg.postDelayed({ tvMsg.visibility = View.GONE }, delay)
    }

    fun hideMsg() {
        tvMsg.visibility = View.GONE
    }

}