/*
 * Copyright 2017 Saksham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stonevire.wallup.utils

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity

import com.stonevire.wallup.callbacks.MessageEvent

import org.greenrobot.eventbus.EventBus

/**
 * Created by Saksham on 8/30/2017.
 */

class Permissions : AppCompatActivity() {

    override fun onResume() {
        super.onResume()

        storagePermission()
    }

    fun storagePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
            } else {
                EventBus.getDefault().post(MessageEvent("Storage Permission Available"))
                finish()
            }
        } else {
            EventBus.getDefault().post(MessageEvent("Storage Permission Available"))
            finish()
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this@Permissions)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel") { dialog, which ->
                    EventBus.getDefault().post(MessageEvent("Permission Denied"))
                    finish()
                }
                .create()
                .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //Storage Permission

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                EventBus.getDefault().post(MessageEvent("Storage Permission Available"))
                finish()

            } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) {

                showMessageOKCancel("Please allow access to Storage for storing Images ... The app doesn't access files/photos" + " other than your local Wallup Images ",
                        DialogInterface.OnClickListener { dialog, which ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", packageName, null))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                            startActivity(intent)
                            finish()
                        })
            } else {
                EventBus.getDefault().post(MessageEvent("Permission Denied"))
                finish()
            }
        }
    }
}
