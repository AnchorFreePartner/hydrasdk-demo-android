package com.anchorfree.hydrasdk.rx

import android.os.Parcelable
import com.anchorfree.sdk.SessionInfo
import com.anchorfree.sdk.UnifiedSDK
import com.anchorfree.vpnsdk.vpnservice.VPNState
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

fun UnifiedSDK.observeVpnState(): Observable<VPNState> = Observable.create {
    val listener = RxVpnStateListener(it)
    UnifiedSDK.addVpnStateListener(listener)
    it.setCancellable { UnifiedSDK.removeVpnStateListener(listener) }
}

fun <T : Parcelable> UnifiedSDK.observeVpnCalls(): Observable<T> = Observable.create {
    val listener = RxVpnCallback<T>(it)
    UnifiedSDK.addVpnCallListener(listener)
    it.setCancellable { UnifiedSDK.removeVpnCallListener(listener) }
}

fun UnifiedSDK.getStatus(): Single<SessionInfo> = Single.create {
    val listener = RxSingleCallback<SessionInfo>(it)
    UnifiedSDK.getStatus(listener)
}

fun UnifiedSDK.observeTraffic(): Observable<Traffic> = Observable.create {
    val listener = RxTrafficCallback(it)
    UnifiedSDK.addTrafficListener(listener)
    it.setCancellable {
        UnifiedSDK.removeTrafficListener(listener)
    }
}
