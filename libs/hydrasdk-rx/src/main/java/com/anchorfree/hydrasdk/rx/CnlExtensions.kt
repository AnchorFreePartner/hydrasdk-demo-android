package com.anchorfree.hydrasdk.rx

import com.anchorfree.sdk.CNL
import com.anchorfree.sdk.CnlConfig
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

fun CNL.loadList(): Single<List<CnlConfig>> = Single.create {
    val listener = RxSingleCallback<List<CnlConfig>>(it)
    this.loadList(listener)
}

fun CNL.updateList(configs: List<CnlConfig>): Completable = Completable.create {
    val listener = RxCompletableCallback(it)
    this.updateList(configs, listener)
}

fun CNL.clear(): Completable = Completable.create {
    val listener = RxCompletableCallback(it)
    this.clear(listener)
}
