package com.northghost.hydraclient

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.os.Build
import android.text.TextUtils
import android.util.Log
import co.pango.detectfrida.DetectFrida
import unified.vpn.sdk.CompletableCallback
import unified.vpn.sdk.HydraTransportConfig
import unified.vpn.sdk.OpenVpnTransportConfig
import unified.vpn.sdk.SdkNotificationConfig
import unified.vpn.sdk.TransportConfig
import unified.vpn.sdk.UnifiedSdk
import unified.vpn.sdk.WireTransportConfig

class MainApplication: Application () {
    companion object {
        const val CHANNEL_ID: String = "vpn"
    }
    override fun onCreate() {
        super.onCreate()
        DetectFrida().detect{
            throw IllegalStateException("Hooking framework has been detected")
        }
        initHydraSdk()
    }

    fun initHydraSdk() {
        createNotificationChannel()
        val prefs = getPrefs()

        val transportConfigList: MutableList<TransportConfig> = ArrayList()
        transportConfigList.add(HydraTransportConfig.create())
        transportConfigList.add(WireTransportConfig.create())
        transportConfigList.add(OpenVpnTransportConfig.tcp())
        transportConfigList.add(OpenVpnTransportConfig.udp())
        UnifiedSdk.update(transportConfigList, CompletableCallback.EMPTY)

        val notificationConfig = SdkNotificationConfig.newBuilder()
            .title(resources.getString(R.string.app_name))
            .channelId(CHANNEL_ID)
            .build()
        UnifiedSdk.update(notificationConfig)

        UnifiedSdk.setLoggingLevel(Log.VERBOSE)
    }

    fun setNewHostAndCarrier(hostUrl: String?, carrierId: String?) {
        val prefs = getPrefs()
        if (TextUtils.isEmpty(hostUrl)) {
            prefs.edit().remove(BuildConfig.STORED_HOST_URL_KEY).apply()
        } else {
            prefs.edit().putString(BuildConfig.STORED_HOST_URL_KEY, hostUrl).apply()
        }

        if (TextUtils.isEmpty(carrierId)) {
            prefs.edit().remove(BuildConfig.STORED_CARRIER_ID_KEY).apply()
        } else {
            prefs.edit().putString(BuildConfig.STORED_CARRIER_ID_KEY, carrierId).apply()
        }
        initHydraSdk()
    }

    fun getPrefs(): SharedPreferences {
        return getSharedPreferences(BuildConfig.SHARED_PREFS, MODE_PRIVATE)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Sample VPN"
            val description = "VPN notification"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}