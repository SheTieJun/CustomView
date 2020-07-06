package me.shetj.customviewdemo.pic

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


fun AppCompatActivity.hasPicFeature(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
    } else {
        false
    }
}

/**
 * 更新PicInPic的params
 */
@RequiresApi(Build.VERSION_CODES.O)
fun AppCompatActivity.updatePicInPicParams(builder: PictureInPictureParams.Builder){
    setPictureInPictureParams(builder.build());
}

@RequiresApi(Build.VERSION_CODES.O)
fun Context.createAction(
    @DrawableRes resId: Int,
    info: String,
    description: String,
    requestCode:Int,
    infoUri: Uri
): RemoteAction {
    return RemoteAction(
        Icon.createWithResource(this, resId),
        info,
        description,
        PendingIntent.getActivity(
            this,
            requestCode,
            Intent(Intent.ACTION_VIEW, infoUri),
            0
        )
    )
}
