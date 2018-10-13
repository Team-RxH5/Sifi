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
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.*
import com.anagramsoftware.sifi.R
import com.anagramsoftware.sifi.service.SifiBinder
import com.anagramsoftware.sifi.service.SifiService
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity(), ServiceConnection, NavController.OnNavigatedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    // Auth
    private val firebaseAuth: FirebaseAuth by inject()
    private var firebaseUser: FirebaseUser? = null

    private lateinit var navController: NavController

    var service: SifiService? = null
    private var isBound = false
    private var hasFineLocationPermission = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        setContentView(R.layout.activity_main)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            hasFineLocationPermission = false
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_COARSE_LOCATION_PERMISSIONS)
        } else {
            hasFineLocationPermission = true
        }

        navController = findNavController(R.id.nav_host_fragment)
        navController.addOnNavigatedListener(this)

        bottom_navigation.setOnNavigationItemSelectedListener(this)
        bottom_navigation.setOnNavigationItemReselectedListener{}

        when (intent.action) {
            MainActivity.ACTION_PROVIDE -> navController.navigate(R.id.provide_fragment)
            MainActivity.ACTION_USE -> navController.navigate(R.id.use_fragment)
        }

        val intent = Intent(this, SifiService::class.java)
        startService(intent)
        bindService(intent, this, Context.BIND_AUTO_CREATE)

        // Auth
        firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            showAuthUI()
            return
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_COARSE_LOCATION_PERMISSIONS) {
            // If request is cancelled, the result arrays are empty.
            hasFineLocationPermission = (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            return
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        navController.removeOnNavigatedListener(this)
        unbindService(this)
        Log.d(TAG, "onDestroy")
    }

    override fun onNavigated(controller: NavController, destination: NavDestination) {
        Log.d(TAG, "${bottom_navigation.selectedItemId} ${destination.id}")
        if (bottom_navigation.selectedItemId != destination.id)
            bottom_navigation.selectedItemId = destination.id
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        if (navController.currentDestination != null && navController.currentDestination!!.id != p0.itemId) {
            val builder = NavOptions.Builder()
            builder.setClearTask(true)
            navController.navigate(p0.itemId, null, builder.build())
        }
        return true
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        this@MainActivity.service = (service as SifiBinder).service
        isBound = true
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        isBound = false
    }

    fun logout() {
        firebaseAuth.signOut()
        showAuthUI()
    }

    private fun showAuthUI() {
        navController.navigate(R.id.auth_activity)
        finish()
    }

    companion object {
        private const val TAG = "MainActivity"

        private const val REQUEST_COARSE_LOCATION_PERMISSIONS = 0

        const val ACTION_PROVIDE = "com.anagramsoftware.sifi.ACTION_PROVIDE"
        const val ACTION_USE = "com.anagramsoftware.sifi.ACTION_USE"
    }

}
