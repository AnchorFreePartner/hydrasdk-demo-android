package com.anchorfree.hydrasdk.rx

import com.anchorfree.reporting.TrackingConstants.GprReason
import com.anchorfree.sdk.SessionConfig
import com.anchorfree.sdk.VPN
import io.reactivex.rxjava3.core.Completable

fun VPN.asReactive(): VpnRx = VpnRxImpl(this)

interface VpnRx {
    fun start(config: SessionConfig): Completable

    fun restart(config: SessionConfig): Completable

    fun stop(@GprReason reason: String): Completable

    fun updateConfig(config: SessionConfig): Completable

    fun startPerformanceTest(ip: String, config: String): Completable

    fun abortPerformanceTest(): Completable
}

private class VpnRxImpl(private val vpn: VPN) : VpnRx {
    override fun start(config: SessionConfig): Completable = Completable.create {
        vpn.start(config, RxCompletableCallback(it))
    }

    override fun restart(config: SessionConfig): Completable = Completable.create {
        vpn.restart(config, RxCompletableCallback(it))
    }

    override fun stop(@GprReason reason: String): Completable = Completable.create {
        vpn.stop(reason, RxCompletableCallback(it))
    }

    override fun updateConfig(config: SessionConfig): Completable = Completable.create {
        vpn.updateConfig(config, RxCompletableCallback(it))
    }

    override fun startPerformanceTest(ip: String, config: String): Completable = Completable.create {
        vpn.startPerformanceTest(ip, config, RxCompletableCallback(it))
    }

    override fun abortPerformanceTest(): Completable = Completable.create {
        vpn.abortPerformanceTest(RxCompletableCallback(it))
    }
}