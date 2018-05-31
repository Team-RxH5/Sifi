package com.anagramsoftware.sifi

import android.app.Application
import com.anagramsoftware.sifi.di.appModule
import org.koin.android.ext.android.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(appModule)
    }
}