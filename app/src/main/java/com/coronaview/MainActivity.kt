package com.coronaview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback : LocationCallback

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* Initializam locationCLient */
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        /* Initializam harta */
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        val locationRequest = LocationRequest.create()?.apply {
            interval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest!!)
        val client : SettingsClient = LocationServices.getSettingsClient(this)
        val task : Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                Toast.makeText(this@MainActivity, "TRIGGERED", Toast.LENGTH_LONG).show()
                Log.w("NEWLOCATION", "TRIGGERED")
                locationResult ?: return
                for (location in locationResult.locations){
                    Log.w( "NEWLOCATION","${location.latitude} ${location.longitude}")
                    val latLong = LatLng(location.latitude, location.longitude)
                    addMarker(
                            MarkerOptions()
                                    .position(latLong)
                                    .title("Marker in last location")
                    )
                }
            }
        }

        task.addOnSuccessListener { locationSettingsResponse ->
            Log.w("TASK", "SUCCESS")
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper())
        }

        task.addOnFailureListener{ exception ->
            if (exception is ResolvableApiException){
                try {
                    exception.startResolutionForResult(this@MainActivity, 0x1)
                } catch (sendEx : IntentSender.SendIntentException) {}
            }
        }

    }

   override fun onMapReady(googleMap: GoogleMap?) {
//        googleMap?.apply {
////                var lastLocation: Location? = null
////                fusedLocationClient.lastLocation
////                        .addOnSuccessListener { location: Location? ->
////                            lastLocation = location
////                        }
//        }
//
//        val googleApiClient = null
//        val lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)

//        if (lastLocation == null) {
//            val latitude: Double = lastLocation!!.getLatitude()
//            val longitude: Double = lastLocation!!.getLongitude()
//
//        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 0)
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        googleMap!!.setMyLocationEnabled(true)
    }

    private fun addMarker(admarker: MarkerOptions?) {

    }

}










