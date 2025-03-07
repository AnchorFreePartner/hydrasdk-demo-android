package com.northghost.hydraclient.extensions

import android.app.AlarmManager
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.northghost.hydraclient.R

fun AppCompatActivity.showAlarmPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.permission_dialog_message).setPositiveButton(
            getString(R.string.permission_dialog_positive)
        ) { dialog, _ ->
            startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            dialog.dismiss()
        }.setNegativeButton(R.string.permission_dialog_negative) { dialog, _ ->
            dialog.dismiss()
        }.create().show()
}

fun AppCompatActivity.scheduleAlarmPermissionGranted() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
        applicationContext.getSystemService(AlarmManager::class.java)
            .canScheduleExactAlarms()
