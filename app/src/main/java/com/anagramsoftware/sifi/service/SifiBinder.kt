package com.anagramsoftware.sifi.service

import android.os.Binder
import java.lang.ref.WeakReference

class SifiBinder(val service: SifiService): Binder()