package com.northghost.hydraclient.extensions

import android.app.AlarmManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.northghost.hydraclient.R
import com.scottyab.rootbeer.RootBeer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

fun AppCompatActivity.showDialogIfRooted(context: Context) {
    CoroutineScope(Dispatchers.IO).launch{
        val rootBeer = RootBeer(context)
        val  rootResult =
            listOf(
                Pair("Root Management Apps", rootBeer.detectRootManagementApps()),
                Pair("Potentially Dangerous Apps", rootBeer.detectPotentiallyDangerousApps()),
                Pair("Root Cloaking Apps", rootBeer.detectRootCloakingApps()),
                Pair("TestKeys", rootBeer.detectTestKeys()),
                Pair("BusyBoxBinary", rootBeer.checkForBusyBoxBinary()),
                Pair("SU Binary", rootBeer.checkForSuBinary()),
                Pair("2nd SU Binary check", rootBeer.checkSuExists()),
                Pair("For RW Paths", rootBeer.checkForRWPaths()),
                Pair("Dangerous Props", rootBeer.checkForDangerousProps()),
                Pair("Root via native check", rootBeer.checkForRootNative()),
                Pair("Magisk specific checks", rootBeer.checkForMagiskBinary()),
            ).filter { it.second }.joinToString(separator = "\n") { it.first }
        if (rootResult.isNotEmpty()) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Device is rooted").setMessage("Root status triggered via:${rootResult}").setNegativeButton("OK"){ dialog, _ ->
                dialog.dismiss()
            }
            withContext(Dispatchers.Main) {
                builder.show()
            }
        }
    }
}