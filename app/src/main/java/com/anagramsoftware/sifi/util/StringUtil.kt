package com.anagramsoftware.sifi.util

import java.util.*

fun generateRandomString(length: Int): String {
    val s = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
    val salt = StringBuilder()
    val rnd = Random()
    while (salt.length < length) { // length of the random string.
        val index = (rnd.nextFloat() * s.length).toInt()
        salt.append(s[index])
    }
    return salt.toString()

}