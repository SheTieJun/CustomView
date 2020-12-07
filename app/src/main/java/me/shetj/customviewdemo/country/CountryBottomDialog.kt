package me.shetj.customviewdemo.country

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import me.shetj.base.ktx.showToast
import me.shetj.base.tools.app.ArmsUtils
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.base.SBaseBottomSheetDialog
import me.shetj.customviewdemo.databinding.DialogCounrtyBinding
import java.util.*


class CountryBottomDialog(private val context: Context, list: MutableList<AreaCodeModel>) :
    SBaseBottomSheetDialog<DialogCounrtyBinding>() {

    private val mAdapter: PhoneAreaCodeAdapter

    init {
        mAdapter = PhoneAreaCodeAdapter(list).apply {
            setOnItemClickListener { _, _, position ->
                getItem(position).tel?.showToast()
            }
            addHeaderView(getHeaderView(list))
        }
    }


    override fun buildBottomSheetDialog(): BottomSheetDialog {
        return BottomSheetDialog(
            context,
            R.style.Theme_MaterialComponents_Light_BottomSheetDialog
        ).apply {
            mViewBinding = DialogCounrtyBinding.inflate(layoutInflater)
            setContentView(mViewBinding.root)
            mViewBinding.iRecyclerView.apply {
                adapter = mAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }


    fun getHeaderView(list: MutableList<AreaCodeModel>): View {
        val view = LayoutInflater.from(context).inflate(R.layout.head_area_code, null)
        val country = Locale.getDefault().country
        list.find { it.shortName.equals(country, true) }?.apply {
            view.findViewById<TextView>(R.id.position).text = this.name
            view.findViewById<TextView>(R.id.code).text = "+${this.tel}"
        }
        return view
    }


}