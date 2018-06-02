package com.anagramsoftware.sifi.service

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.util.Log


class HotspotManager(private val context: Context) {
    private val mWifiManager: WifiManager = this.context.getSystemService(Context.WIFI_SERVICE) as WifiManager

    val isHotshotOn: Boolean
        get() {
            try {
                val method = mWifiManager.javaClass.getDeclaredMethod("isWifiApEnabled")
                method.isAccessible = true
                return method.invoke(mWifiManager) as Boolean
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return false
        }

    fun getCurrentAp(): WifiConfiguration? {
        return try {
            val method = mWifiManager.javaClass.getMethod("getWifiApConfiguration")
            method.invoke(mWifiManager) as WifiConfiguration
        } catch (e: Exception) {
            Log.e(this.javaClass.toString(), "", e)
            null
        }

    }

    fun setNewAp(wifiConfig: WifiConfiguration): Boolean {
        mWifiManager.isWifiEnabled = false // turn off Wifi
        if (isHotshotOn) {
            setWifiApEnabled(false)
        } else {
            Log.e(TAG, "WifiAp is turned off")
        }
        try {
            val method = mWifiManager.javaClass.getMethod("setWifiApConfiguration", WifiConfiguration::class.java)
            return method.invoke(mWifiManager, wifiConfig) as Boolean
        } catch (e: Exception) {
            Log.e(this.javaClass.toString(), "", e)
        }
        return false
    }

    fun setWifiApEnabled(enabled: Boolean, wifiConfig: WifiConfiguration? = null): Boolean {
        return try {
            if (enabled) { // disable WiFi in any case
                mWifiManager.isWifiEnabled = false
            }

            val method = mWifiManager.javaClass.getMethod("setWifiApEnabled", WifiConfiguration::class.java, Boolean::class.javaPrimitiveType)
            method.invoke(mWifiManager, wifiConfig, enabled) as Boolean
        } catch (e: Exception) {
            Log.e(this.javaClass.toString(), "", e)
            false
        }

    }

    companion object {
        private const val TAG = "ApManager"

        fun buildConfig(ssid: String, password: String): WifiConfiguration {
            val wifiConfig = WifiConfiguration()
            wifiConfig.SSID = ssid
            wifiConfig.preSharedKey = password
            wifiConfig.allowedKeyManagement.set(4)
            wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
            return wifiConfig
        }
    }

}