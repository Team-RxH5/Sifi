package com.anagramsoftware.sifi.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.anagramsoftware.sifi.R
import com.anagramsoftware.sifi.service.SifiBinder
import com.anagramsoftware.sifi.service.SifiService

class MainActivity : AppCompatActivity(), ServiceConnection {

    var service: SifiService? = null
    private var isBound = false
    private var hasFineLocationPermission = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            hasFineLocationPermission = false
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_COARSE_LOCATION_PERMISSIONS)
        } else {
            hasFineLocationPermission = true
        }

        val navController = findNavController(R.id.nav_host_fragment)

        val navigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        navigationView.setupWithNavController(navController)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_COARSE_LOCATION_PERMISSIONS) {
            // If request is cancelled, the result arrays are empty.
            hasFineLocationPermission = (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            return
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, SifiService::class.java)
        bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unbindService(this)
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        this@MainActivity.service = (service as SifiBinder).service
        isBound = true
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        isBound = false
    }

    companion object {
        private const val REQUEST_COARSE_LOCATION_PERMISSIONS = 0
    }

}
