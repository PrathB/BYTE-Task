package com.test.bytetask

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.permissionx.guolindev.PermissionX
import com.test.bytetask.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding : ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.byteLogo?.setShadowLayer(
            15f,
            0f,
            0f,
            Color.parseColor("#66FF00")
        )

        binding?.flStart?.setOnClickListener {
            PermissionX.init(this)
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .onForwardToSettings { scope, deniedList ->
                    scope.showForwardToSettingsDialog(deniedList, "Please grant permission to access" +
                            " precise location in App Settings", "OK", "Cancel")
                }
                .request { allGranted, _, deniedList ->
                    if (allGranted) {
                        if(isLocationEnabled()){
                            binding?.byteLogo?.text="Location Enabled"
                        }
                        else{
                            showEnableLocationDialog()
                        }
                    }else{
                        Toast.makeText(this, "Permission Denied: $deniedList", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
    private fun isLocationEnabled() : Boolean{
        val locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE)
                as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    private fun showEnableLocationDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Location Services Disabled")
        alertDialog.setMessage("Please enable location services to use this feature.")
        alertDialog.setPositiveButton("Enable") { _, _ ->
            // Open the device settings to enable location
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
        }
        alertDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

}