package com.anagramsoftware.sifi

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.anagramsoftware.sifi.di.appModule
import org.koin.android.ext.android.startKoin

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        startKoin(appModule)
    }
}