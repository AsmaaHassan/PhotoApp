package com.example.weatherapp.utils

import android.R
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import java.util.*

const val LOCATION_PERMISSION_REQUEST_CODE = 110
const val MULTIPLE_PERMISSION_REQUEST_CODE = 100

class PermissionUtils {
    companion object {
        private var permissionDialog: AlertDialog? = null
        fun isAllPermissionGranted(
            fragment: Fragment,
            permissions: Array<String>
        ): Boolean {
            var isAllPermissionsGranted = true
            for (permission in permissions) {
                if (!isPermissionGranted(
                        fragment,
                        permission
                    )
                ) {
                    isAllPermissionsGranted = false
                    break
                }
            }
            return isAllPermissionsGranted
        }


        fun isPermissionGranted(
            fragment: Fragment,
            permission: String?
        ): Boolean {
            return (ActivityCompat.checkSelfPermission(fragment.activity!!, permission!!)
                    == PackageManager.PERMISSION_GRANTED)
        }

        fun checkForPermission(
            fragment: Fragment,
            permission: String,
            requestCode: Int
        ) {
            val permissionsNeeded =
                ArrayList<String>()
            if (ActivityCompat.checkSelfPermission(fragment.activity!!, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNeeded.add(permission)
            }
            if (!permissionsNeeded.isEmpty()) {
                fragment.requestPermissions(
                    permissionsNeeded.toTypedArray(),
                    requestCode
                )
            }
        }


        private fun requestPermission(
            fragment: Fragment,
            permissions: Array<String>,
            requestCode: Int
        ) {
            fragment.requestPermissions(permissions, requestCode)
        }

        fun checkForPermissions(
            fragment: Fragment,
            permissions: Array<String>
        ) {
            val permissionsNeeded =
                ArrayList<String>()
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(fragment.activity!!, permission)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionsNeeded.add(permission)
                }
            }
            if (!permissionsNeeded.isEmpty()) {
                fragment.requestPermissions(
                    permissionsNeeded.toTypedArray(),
                    MULTIPLE_PERMISSION_REQUEST_CODE
                )
            }
        }


        /*fun showApplicationSettingsDialog(context: Activity) {
            if (permissionDialog == null) {
                val builder =
                    AlertDialog.Builder(context, R.style.AlertDialogStyle)
                builder.setTitle(context.getString(R.string.permission_dialog_title))
                    .setMessage(context.getString(R.string.msg_permission_required))
                    .setPositiveButton(
                        context.getString(R.string.open_settings)
                    ) { dialog: DialogInterface?, which: Int ->
                        // continue with delete
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri =
                            Uri.fromParts("package", context.packageName, null)
                        intent.data = uri
                        context.startActivity(intent)
                    }
                    .setNegativeButton(
                        R.string.cancel
                    ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }
                permissionDialog = builder.create()
                permissionDialog!!.setCanceledOnTouchOutside(
                    false
                )
            }
            if (permissionDialog!!.isShowing()) {
                permissionDialog!!.show()
                permissionDialog!!.getButton(
                    DialogInterface.BUTTON_NEGATIVE
                ).setTextColor(context.resources.getColor(R.color.darker_gray))
                permissionDialog!!.getButton(
                    DialogInterface.BUTTON_POSITIVE
                ).setTextColor(context.resources.getColor(R.color.darker_gray))
            }
        }

*/
    }


}