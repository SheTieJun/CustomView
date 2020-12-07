package me.shetj.customviewdemo.base

import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

abstract class SBaseBottomSheetDialog<VB : ViewBinding> {

    private val bottomSheetDialog by lazy { buildBottomSheetDialog() }

    lateinit var mViewBinding: VB

    abstract fun buildBottomSheetDialog(): BottomSheetDialog

    open fun showBottomSheet() {
        if (!bottomSheetDialog.isShowing) {
            bottomSheetDialog.show()
        }
    }

    open fun dismissBottomSheet() {
        if (bottomSheetDialog.isShowing) bottomSheetDialog.dismiss()
    }
}