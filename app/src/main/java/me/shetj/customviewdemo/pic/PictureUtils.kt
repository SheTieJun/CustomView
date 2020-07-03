package me.shetj.customviewdemo.pic

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity


fun AppCompatActivity.hasPicFeature(): Boolean {
   return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
    }else{
       false
   }
}