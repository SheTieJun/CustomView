package me.shetj.customviewdemo.lihuaindex

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import me.shetj.base.mvvm.BaseBindingFragment
import me.shetj.base.mvvm.BaseViewModel
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.databinding.FragmentIndexBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [IndexFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class IndexFragment : BaseBindingFragment<BaseViewModel, FragmentIndexBinding>() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        mViewBinding.viewPager.isUserInputEnabled = true //开启滚动
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
        }.setViewPager(mViewBinding.viewPager, arrayListOf("全部课程","接单市集"),requireActivity(),
            arrayListOf(UserFragment.newInstance("1","2"), LearnFragment.newInstance("1","2")))
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IndexFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            IndexFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun initEventAndData() {
    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentIndexBinding {
        return FragmentIndexBinding.inflate(inflater, container, false)
    }
}