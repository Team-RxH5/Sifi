package com.anagramsoftware.sifi.service

import android.app.Service
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.TrafficStats
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.IBinder
import android.preference.PreferenceManager
import android.util.Log
import com.anagramsoftware.sifi.data.model.Hotspot
import com.anagramsoftware.sifi.data.model.Traffic
import com.anagramsoftware.sifi.util.generateRandomString
import com.cantrowitz.rxbroadcast.RxBroadcast
import com.scottyab.aescrypt.AESCrypt
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.security.GeneralSecurityException
import java.util.concurrent.TimeUnit

class SifiService : Service() {

    private val binder = SifiBinder(this)

    private lateinit var hotspotManager: HotspotManager
    private lateinit var wifiManager: WifiManager

    private val wifiStateDisposable = CompositeDisposable()
    private var trafficDisposable: Disposable? = null

    var trafficStart: Traffic? = null
    val traffic = MutableLiveData<Traffic>()
    var connectedTo: Hotspot? = null

    override fun onCreate() {
        super.onCreate()
        hotspotManager = HotspotManager(this)
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val filter = IntentFilter()
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)

        wifiStateDisposable.add(RxBroadcast.fromBroadcast(application, filter)
                .subscribe {
                    when(it.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)) {
                        WifiManager.WIFI_STATE_ENABLED -> {
                            val info = wifiManager.connectionInfo
                            decrypt(info.ssid)?.let {
                                connectedTo = Hotspot(info.ssid, it[0], it[1], WifiManager.calculateSignalLevel(info.rssi, 5))
                                startTracking()
                            }
                        }
                        WifiManager.WIFI_STATE_DISABLED -> {
                            connectedTo = null
                            stopTracking()
                        }
                    }
                })
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        wifiStateDisposable.dispose()
        stopTracking()
    }

    // Provide
    fun startProviding() {
        val password = generateRandomString(10)
        val rawSSID = "sifi|$password"
        val crypt = AESCrypt.encrypt(KEY, rawSSID)
        Log.d(TAG, crypt.length.toString())
        val userWifiConfig = hotspotManager.getCurrentAp()
        userWifiConfig?.let {
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(KEY_USER_SSID, it.SSID)
                    .putString(KEY_USER_PASSWORD, it.preSharedKey)
                    .putBoolean(KEY_USER_AP_ENABLED, isHotspotActive())
                    .apply()
        }
        val newWifiConfig = HotspotManager.buildConfig(crypt, password)
        hotspotManager.setNewAp(newWifiConfig)
        hotspotManager.setWifiApEnabled(true, newWifiConfig)
    }

    fun stopProviding() {
        PreferenceManager.getDefaultSharedPreferences(this).also {
            val ssid = it.getString(KEY_USER_SSID, "AndroidAP")
            val pass = it.getString(KEY_USER_PASSWORD, "00000000")
            val enabled = it.getBoolean(KEY_USER_AP_ENABLED, false)
            val userWifiConfig = HotspotManager.buildConfig(ssid, pass)
            hotspotManager.setNewAp(userWifiConfig)
            hotspotManager.setWifiApEnabled(enabled, userWifiConfig)
        }
    }

    fun isHotspotActive(): Boolean {
        return hotspotManager.isHotshotOn
    }

    // Use
    fun connect(hotspot: Hotspot): Boolean {
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = "\"${hotspot.SSID}\""
        wifiConfig.preSharedKey = "\"${hotspot.pass}\""
        val netId = wifiManager.addNetwork(wifiConfig)
        wifiManager.disconnect()
        val enabled = wifiManager.enableNetwork(netId, true)
        val connected = wifiManager.reconnect()
        if (enabled && connected){
            this.connectedTo = hotspot
            startTracking()
        }
        return connected
    }

    fun disconnect(): Boolean {
        val disconnected = wifiManager.disconnect()
        if (disconnected) {
            this.connectedTo = null
            stopTracking()
        }
        return disconnected
    }

    fun isConnected() = connectedTo != null

    fun startWifiScan() {
        wifiManager.let {
            if (!it.isWifiEnabled) {
                Log.d(TAG, "Wifi is disabled..making it enabled")
                it.isWifiEnabled = true
            }
            it.startScan()
        }
    }

    fun getWifiSCanResult(): Observable<List<Hotspot>> {
        return Observable.just(wifiManager.scanResults)
                .map {
                    it.map {
                        val strings = decrypt(it.SSID)
                        if (strings != null) {
                            val level = WifiManager.calculateSignalLevel(it.level, 5)
                            Hotspot(it.SSID, strings[0], strings[1], level)
                        } else {
                            Hotspot()
                        }
                    }.filter { it.SSID != "" }
                }
    }

    private fun decrypt(SSID: String): List<String>? {
        return if (SSID.length == 24) {
            try {
                AESCrypt.decrypt(KEY, SSID).split("|")
            } catch (e: GeneralSecurityException) {
                Log.d(TAG, "error $e")
                null
            } catch (e: NullPointerException) {
                null
            }
        } else {
            null
        }
    }

    // Tracking
    private fun startTracking() {
        stopTracking()
        trafficDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .map{
                    Traffic(TrafficStats.getTotalTxBytes(), TrafficStats.getTotalRxBytes())
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (trafficStart != null) {
                        traffic.value = Traffic(it.sent - trafficStart!!.sent, it.received - trafficStart!!.received)
                    } else {
                        trafficStart = it
                        traffic.value = Traffic(0, 0)
                    }
                }
    }

    private fun stopTracking() {
        trafficDisposable?.let {
            if (!it.isDisposed)
                it.dispose()
        }
        traffic.value = null
    }

    companion object {
        private const val TAG = "SifiService"
        private const val KEY = "accdiec"

        private const val KEY_USER_SSID = "user_ssid"
        private const val KEY_USER_PASSWORD = "user_pass"
        private const val KEY_USER_AP_ENABLED = "user_ap_enabled"
    }
}
