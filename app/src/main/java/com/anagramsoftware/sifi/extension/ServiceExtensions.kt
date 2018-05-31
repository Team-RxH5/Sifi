package com.anagramsoftware.sifi.extension

import android.support.v4.app.Fragment
import com.anagramsoftware.sifi.service.SifiService
import com.anagramsoftware.sifi.ui.MainActivity

fun Fragment.getService() : SifiService? {
    return if (activity is MainActivity) {
        (activity as MainActivity).service
    } else null
}