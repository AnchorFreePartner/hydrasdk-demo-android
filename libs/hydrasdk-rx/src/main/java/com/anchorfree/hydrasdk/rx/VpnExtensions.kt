package com.anchorfree.hydrasdk.rx

import com.anchorfree.reporting.TrackingConstants.GprReason
import com.anchorfree.sdk.SessionConfig
import com.anchorfree.sdk.VPN
import io.reactivex.rxjava3.core.Completable

fun VPN.start(config: SessionConfig): Completable = Completable.create {
    this.start(config, RxCompletableCallback(it))
}

fun VPN.restart(config: SessionConfig): Completable = Completable.create {
    this.restart(config, RxCompletableCallback(it))
}

fun VPN.stop(@GprReason reason: String): Completable = Completable.create {
    this.stop(reason, RxCompletableCallback(it))
}

fun VPN.updateConfig(config: SessionConfig): Completable = Completable.create {
    this.updateConfig(config, RxCompletableCallback(it))
}

fun VPN.startPerformanceTest(ip: String, config: String) = Completable.create {
    this.startPerformanceTest(ip, config, RxCompletableCallback(it))
}

fun VPN.abortPerformanceTest(): Completable = Completable.create {
    this.abortPerformanceTest(RxCompletableCallback(it))
}
