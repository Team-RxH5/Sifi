package com.anagramsoftware.sifi.ui.use

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.IntentFilter
import android.net.TrafficStats
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.util.Log
import com.anagramsoftware.sifi.data.model.Hotspot
import com.anagramsoftware.sifi.data.model.Traffic
import com.cantrowitz.rxbroadcast.RxBroadcast
import com.scottyab.aescrypt.AESCrypt
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.security.GeneralSecurityException
import java.util.concurrent.TimeUnit


class UseViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "UseViewModel"
    }

}
