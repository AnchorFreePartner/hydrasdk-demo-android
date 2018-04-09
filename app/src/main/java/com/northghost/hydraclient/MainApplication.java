package com.northghost.hydraclient;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.anchorfree.hydrasdk.HydraSDKConfig;
import com.anchorfree.hydrasdk.HydraSDKConfigFactory;
import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.api.ClientInfo;
import com.anchorfree.hydrasdk.utils.LogDelegate;
import com.anchorfree.hydrasdk.utils.Logger;
import com.anchorfree.hydrasdk.vpnservice.connectivity.NotificationConfig;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initHydraSdk();
    }

    public void initHydraSdk() {
        SharedPreferences prefs = getPrefs();
        ClientInfo clientInfo = ClientInfo.newBuilder()
                .baseUrl(prefs.getString(BuildConfig.STORED_HOST_URL_KEY, BuildConfig.BASE_HOST))
                .carrierId(prefs.getString(BuildConfig.STORED_CARRIER_ID_KEY, BuildConfig.BASE_CARRIER_ID))
                .checkCaptive(false)
                .build();

        NotificationConfig notificationConfig = NotificationConfig.newBuilder()
                .title(getResources().getString(R.string.app_name))
                .enableConnectionLost()
                .build();

        HydraSdk.setLoggingEnabled(true);
        Logger.setLogDelegate(new LogDelegate() {
            @Override
            public void d(String s, String s1) {
                Log.d(s, s1);
            }

            @Override
            public void v(String s, String s1) {
                Log.v(s, s1);
            }

            @Override
            public void i(String s, String s1) {
                Log.i(s, s1);
            }

            @Override
            public void e(String s, String s1) {
                Log.e(s, s1);
            }

            @Override
            public void w(String s, String s1) {
                Log.w(s, s1);
            }

            @Override
            public void w(String s, String s1, Throwable throwable) {
                Log.w(s, s1, throwable);
            }

            @Override
            public void e(String s, String s1, Throwable throwable) {
                Log.e(s, s1, throwable);
            }
        });

        HydraSdk.init(this, clientInfo, notificationConfig, HydraSDKConfigFactory.create());
    }

    public void setNewHostAndCarrier(String hostUrl, String carrierId) {
        SharedPreferences prefs = getPrefs();
        if (TextUtils.isEmpty(hostUrl)) {
            prefs.edit().remove(BuildConfig.STORED_HOST_URL_KEY).apply();
        } else {
            prefs.edit().putString(BuildConfig.STORED_HOST_URL_KEY, hostUrl).apply();
        }

        if (TextUtils.isEmpty(carrierId)) {
            prefs.edit().remove(BuildConfig.STORED_CARRIER_ID_KEY).apply();
        } else {
            prefs.edit().putString(BuildConfig.STORED_CARRIER_ID_KEY, carrierId).apply();
        }
        initHydraSdk();
    }

    public SharedPreferences getPrefs() {
        return getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);
    }
}
