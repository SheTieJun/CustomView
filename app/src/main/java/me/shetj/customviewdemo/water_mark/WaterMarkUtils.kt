package me.shetj.customviewdemo.water_mark

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.shetj.base.ktx.showToast
import me.shetj.base.sim.SimpleCallBack
import me.shetj.base.tools.file.EnvironmentStorage
import me.shetj.base.tools.image.ImageUtils
import me.shetj.custom.WaterMarkImage
import me.shetj.customviewdemo.R
import me.shetj.customviewdemo.utils.createDialog
import java.io.File


fun showWaterMarkDialog(context: AppCompatActivity, url:Uri  = Uri.fromFile(File("/storage/emulated/0/DCIM/Camera/IMG_20200712_191119.jpg" ))) {

    createDialog(context, R.layout.layout_water_mark) {
        val image = it.findViewById<WaterMarkImage>(R.id.image)
        it.findViewById<View>(R.id.bt_save).setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                val save = image.save(EnvironmentStorage.downloadCache + "/showWaterMarkDialog.png")
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
    ImageUtils.selectLocalImage(context)
}


fun onImageActivityResult(context: AppCompatActivity,requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == Activity.RESULT_OK) {
        ImageUtils.onActivityResult(
            context as Activity,
            requestCode,resultCode ,data,
            object : SimpleCallBack<Uri>() {
                override fun onSuccess(key: Uri) {
                    showWaterMarkDialog(context,key)
                }
            })
    }
}


