package com.test.bytetask

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.permissionx.guolindev.PermissionX
import com.test.bytetask.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var userId: String? = null
    private var isTracking = false // Track the state of location tracking

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val user = FirebaseAuth.getInstance().currentUser
        userId = user?.uid

        FirebaseApp.initializeApp(this)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Button click listener for starting/stopping location tracking
        binding?.flStart?.setOnClickListener {
            PermissionX.init(this)
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .onForwardToSettings { scope, deniedList ->
                    scope.showForwardToSettingsDialog(
                        deniedList,
                        "Please grant permission to access precise location in App Settings",
                        "OK", "Cancel"
                    )
                }
                .request { allGranted, _, deniedList ->
                    if (allGranted) {
                        if (isLocationEnabled()) {
                            if (isTracking) {
                                stopLocationTracking() // Stop tracking if currently tracking
                            } else {
                                startLocationTracking() // Start tracking if not tracking
                            }
                        } else {
                            showEnableLocationDialog()
                        }
                    } else {
                        Toast.makeText(this, "Permission Denied: $deniedList", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    // Check if location services are enabled
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    // Show dialog to enable location
    private fun showEnableLocationDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Location Services Disabled")
        alertDialog.setMessage("Please enable location services to use this feature.")
        alertDialog.setPositiveButton("Enable") { _, _ ->
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
        }
        alertDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    // Start location tracking
    @SuppressLint("MissingPermission")
    private fun startLocationTracking() {
        isTracking = true
        binding?.tvButton?.text = "Disable Location Tracking"
        binding?.flStart?.setBackgroundResource(R.drawable.start_button_circular_border_ripple_enabled)
        binding?.byteLogo?.setShadowLayer(15f, 0f, 0f, Color.parseColor("#66FF00"))

        val mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).apply {
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
        Toast.makeText(this, "Location tracking enabled", Toast.LENGTH_SHORT).show()
    }

    // Stop location tracking
    private fun stopLocationTracking() {
        isTracking = false
        binding?.tvButton?.text = "Enable Location Tracking"
        binding?.flStart?.setBackgroundResource(R.drawable.start_button_circular_border_ripple)
        binding?.byteLogo?.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)

        // Remove location updates
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
        Toast.makeText(this, "Location tracking disabled", Toast.LENGTH_SHORT).show()
    }

    // Location callback to receive updates
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                val latitude = location.latitude
                val longitude = location.longitude
                updateLocationInFirebase(latitude, longitude)
            }
        }
    }

    // Update location in Firebase
    private fun updateLocationInFirebase(lat: Double, lon: Double) {
        val database = FirebaseDatabase.getInstance()
        val userLocationRef = database.getReference("user_locations").child(userId!!)
        userLocationRef.setValue(UserLocation(lat, lon))
            .addOnSuccessListener {
                Log.d("Firebase", "Location updated successfully")
            }
            .addOnFailureListener {
                Log.e("Firebase", "Failed to update location: ${it.message}")
            }
    }
}
