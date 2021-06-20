package com.example.socialad

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.properties.Delegates

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()
    private lateinit var placename : String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val extras = intent.extras
        if (extras != null) {
            latitude = extras.getString("latitude")!!.toDouble()
            longitude = extras.getString("longitude")!!.toDouble()
            placename = extras.getString("place")!!
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val location = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(location).title(placename))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
    }
}