package com.example.socialad

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.sqrt
import kotlin.properties.Delegates


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {

    private lateinit var mMap: GoogleMap

    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()
    private lateinit var placename : String;

    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null

    private var running = false

    private var stepCount = 0
    private var MagnitudePrevious = 0.0

    private lateinit var tv_stepsTaken : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        resetSteps()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        tv_stepsTaken = findViewById<Button>(R.id.step_count)

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

        val zoomLevel = 14.0f //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel))
    }

    override fun onResume() {
        super.onResume()
        running = true

        if (sensor == null) {
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    @SuppressLint("SetTextI18n")
    fun resetSteps() {
        tv_stepsTaken = findViewById<Button>(R.id.step_count)
        tv_stepsTaken.setOnClickListener {
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        tv_stepsTaken.setOnLongClickListener {

            tv_stepsTaken.text =  "Steps: 0";
            stepCount = 0

            true
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            if (running) {
                var x_acceleration = event.values[0];
                var y_acceleration = event.values[1];
                var z_acceleration = event.values[2];

                var Magnitude = sqrt(x_acceleration * x_acceleration + y_acceleration * y_acceleration + z_acceleration * z_acceleration);
                var MagnitudeDelta = Magnitude - MagnitudePrevious;
                MagnitudePrevious = Magnitude.toDouble();

                if (MagnitudeDelta > 2) {
                    stepCount++;
                }
                tv_stepsTaken.text = "Steps: $stepCount";
            }
        }
    }

}