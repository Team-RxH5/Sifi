package com.anagramsoftware.sifi.ui.selectnetwork

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.anagramsoftware.sifi.data.model.Hotspot
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SelectNetworkViewModel: ViewModel() {

    val hotspots = MutableLiveData<List<Hotspot>>()

    private var hotspotDisposable: Disposable? = null

    fun start() {
    }

    fun stop() {
        dispose()
    }

    fun getResults(results: Observable<List<Hotspot>>) {
        dispose()
        hotspotDisposable = results.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .firstOrError()
                .subscribe({
                    hotspots.value = it
                }, {})
    }

    private fun dispose() {
        hotspotDisposable?.let {
            if (!it.isDisposed)
                it.dispose()
            hotspotDisposable = null
        }
    }
}