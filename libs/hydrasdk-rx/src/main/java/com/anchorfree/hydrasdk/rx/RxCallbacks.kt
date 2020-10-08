package com.anchorfree.hydrasdk.rx

import android.os.Parcelable
import com.anchorfree.vpnsdk.callbacks.*
import com.anchorfree.vpnsdk.exceptions.VpnException
import com.anchorfree.vpnsdk.vpnservice.VPNState
import io.reactivex.rxjava3.core.CompletableEmitter
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.SingleEmitter

class RxSingleCallback<T>(private val emitter: SingleEmitter<T>) : Callback<T> {
    override fun success(result: T) {
        emitter.onSuccess(result)
    }

    override fun failure(e: VpnException) {
        if (emitter.isDisposed.not()) {
            emitter.onError(e)
        }
    }
}

class RxObservableCallback<T>(private val emitter: ObservableEmitter<T>) : Callback<T> {
    override fun success(result: T) {
        emitter.onNext(result)
    }

    override fun failure(e: VpnException) {
        if (emitter.isDisposed.not()) {
            emitter.onError(e)
        }
    }
}

class RxVpnStateListener(private val emitter: ObservableEmitter<VPNState>) : VpnStateListener {
    override fun vpnStateChanged(state: VPNState) {
        emitter.onNext(state)
    }

    override fun vpnError(e: VpnException) {
        if (emitter.isDisposed.not()) {
            emitter.onError(e)
        }
    }
}

class RxTrafficCallback(private val emitter: ObservableEmitter<Traffic>) : TrafficListener {
    override fun onTrafficUpdate(rx: Long, tx: Long) {
        emitter.onNext(Traffic(rx, tx))
    }
}

class RxVpnCallback<T : Parcelable>(private val emitter: ObservableEmitter<T>) : VpnCallback<T> {
    override fun onVpnCall(result: T) {
        emitter.onNext(result)
    }
}

class RxCompletableCallback(private val emitter: CompletableEmitter) : CompletableCallback {
    override fun complete() {
        emitter.onComplete()
    }

    override fun error(e: VpnException) {
        if (emitter.isDisposed.not()) {
            emitter.onError(e)
        }
    }
}

data class Traffic(val bytesRx: Long, val bytesTx: Long)
