package com.anagramsoftware.sifi.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
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

    fun showWritePermissionSettings(force: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (force || !Settings.System.canWrite(this.context)) {
                val intent = Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.parse("package:" + this.context.packageName)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                this.context.startActivity(intent)
            }
        }
    }

    fun turnHotspotOn(): Boolean {
        val wifiConfiguration: WifiConfiguration? = null
        try {
            val method = mWifiManager.javaClass.getMethod("setWifiApEnabled", WifiConfiguration::class.java, Boolean::class.javaPrimitiveType)
            method.invoke(mWifiManager, wifiConfiguration, true)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }

    fun turnHotspotOff(): Boolean {
        try {
            val method = mWifiManager.javaClass.getMethod("setWifiApEnabled", WifiConfiguration::class.java, Boolean::class.javaPrimitiveType)
            method.invoke(mWifiManager, null, false)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }


    fun createNewNetwork(ssid: String, password: String): Boolean {
        mWifiManager.isWifiEnabled = false // turn off Wifi
        if (isHotshotOn) {
            turnHotspotOff()
        } else {
            Log.e(TAG, "WifiAp is turned off")

        }
        val myConfig = WifiConfiguration()
        myConfig.SSID = ssid
        myConfig.preSharedKey = password
        myConfig.allowedKeyManagement.set(4)
        myConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
        try {
            val method = mWifiManager.javaClass.getMethod("setWifiApEnabled", WifiConfiguration::class.java, Boolean::class.javaPrimitiveType)
            return method.invoke(mWifiManager, myConfig, true) as  Boolean
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false

    }

    companion object {
        private const val TAG = "ApManager"
    }

}