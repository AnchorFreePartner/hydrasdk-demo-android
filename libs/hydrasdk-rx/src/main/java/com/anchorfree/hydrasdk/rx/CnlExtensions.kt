package com.anchorfree.hydrasdk.rx

import com.anchorfree.sdk.CNL
import com.anchorfree.sdk.CnlConfig
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

fun CNL.asReactive(): CnlRx = CnlRxImpl(this)

interface CnlRx {
    fun loadList(): Single<List<CnlConfig>>

    fun updateList(configs: List<CnlConfig>): Completable

    fun clear(): Completable
}

private class CnlRxImpl(private val cnl: CNL) : CnlRx {
    override fun loadList(): Single<List<CnlConfig>> = Single.create {
        val listener = RxSingleCallback<List<CnlConfig>>(it)
        cnl.loadList(listener)
    }

    override fun updateList(configs: List<CnlConfig>): Completable = Completable.create {
        val listener = RxCompletableCallback(it)
        cnl.updateList(configs, listener)
    }

    override fun clear(): Completable = Completable.create {
        val listener = RxCompletableCallback(it)
        cnl.clear(listener)
    }
}
