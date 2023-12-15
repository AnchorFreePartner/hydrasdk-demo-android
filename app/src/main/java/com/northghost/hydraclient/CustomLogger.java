package com.northghost.hydraclient;

import android.util.Log;

import androidx.annotation.Keep;

import unified.vpn.sdk.UnifiedLogDelegate;

@Keep
public class CustomLogger extends UnifiedLogDelegate {
    @Override
    public void log(final int priority, Throwable throwable, String tag, String format, Object... args) {
        Log.d(tag, String.format(format,args), throwable);
    }

    @Override
    public void setLogLevel(final int logLevel) {

    }
}