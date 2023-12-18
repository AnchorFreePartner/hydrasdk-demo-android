package com.northghost.hydraclient.deps

import android.util.Log
import unified.vpn.sdk.UnifiedLogDelegate


class KotlinLogger: UnifiedLogDelegate() {
    override fun log(p0: Int, p1: Throwable?, p2: String, p3: String, vararg p4: Any?) {
        Log.d(p2, String.format(p3,p4), p1);
    }

    override fun setLogLevel(p0: Int) {

    }
}