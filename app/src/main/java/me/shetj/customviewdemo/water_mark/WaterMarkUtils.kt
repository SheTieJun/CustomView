package me.shetj.customviewdemo.water_mark

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.shetj.base.base.BaseCallback
import me.shetj.base.ktx.runOnIo
import me.shetj.base.ktx.showToast
import me.shetj.base.tools.file.SDCardUtils
import me.shetj.base.tools.image.ImageUtils
import me.shetj.custom.WaterMarkImage
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.utils.createDialog


fun showWaterMarkDialog(context: AppCompatActivity, url:String  ="/storage/emulated/0/DCIM/Camera/IMG_20200712_191119.jpg" ) {

    createDialog(context, R.layout.layout_water_mark) {
        val image = it.findViewById<WaterMarkImage>(R.id.image)
        it.findViewById<View>(R.id.bt_save).setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val save = image.save(SDCardUtils.downloadCache + "/showWaterMarkDialog.png")
                launch(Dispatchers.Main) {
                    if (save) {
                        "保存成功".showToast()
                    } else {
                        "保存失败".showToast()
                    }
                }
            }
        }
        image.setImage(url)
    }
}


fun selectImage(context: AppCompatActivity){
    ImageUtils.openLocalImage(context)
}


fun onImageActivityResult(context: AppCompatActivity,requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == Activity.RESULT_OK) {
        ImageUtils.onActivityResult(
            context as Activity,
            requestCode,
            data,
            object : BaseCallback<String> {
                override fun onFail(ex: Exception) {
                }

                override fun onSuccess() {
                }

                override fun onSuccess(key: String) {
                    createDialog(context, R.layout.layout_water_mark) {
                        val image = it.findViewById<WaterMarkImage>(R.id.image)
                        image.setImage(key)
                        it.findViewById<View>(R.id.bt_save).setOnClickListener {

                        }
                    }
                }

                override fun onFail() {
                }

            })
    }
}