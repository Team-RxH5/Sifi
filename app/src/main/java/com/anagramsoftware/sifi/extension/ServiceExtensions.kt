package com.anagramsoftware.sifi.extension

import androidx.fragment.app.Fragment
import com.anagramsoftware.sifi.service.SifiService
import com.anagramsoftware.sifi.ui.MainActivity

fun androidx.fragment.app.Fragment.getService() : SifiService? {
    return if (activity is MainActivity) {
        (activity as MainActivity).service
    } else null
}