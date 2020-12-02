package com.example.weatherapp.utils

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.core.content.FileProvider
import java.io.File

class UiUtils {
    companion object {
        fun convertViewToBitmap(v: View): Bitmap? {
            val b = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.RGB_565)
            val c = Canvas(b)
            v.draw(c)
            return b
        }


        fun sharePhoto(activity: Activity, photo: File) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"

            val myPhotoFileUri = FileProvider.getUriForFile(
                activity!!, activity!!.applicationContext.packageName
                        + ".provider", photo
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(Intent.EXTRA_STREAM, myPhotoFileUri)
            activity.startActivity(Intent.createChooser(intent, "Share with..."))
        }
    }
}