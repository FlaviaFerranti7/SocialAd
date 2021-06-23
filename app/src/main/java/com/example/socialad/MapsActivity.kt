package com.example.socialad

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import kotlin.math.sqrt
import kotlin.properties.Delegates

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions

import java.net.URL

import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import com.beust.klaxon.*
import com.google.gson.internal.Streams.parse
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.logging.Level.parse


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {

    private lateinit var mMap: GoogleMap

    private var latitude by Delegates.notNull<Double>()
    private var longitude by Delegates.notNull<Double>()
    private lateinit var placename : String;

    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null

    private var running = false

    private var stepCount = 0
    private var stepToMake : Int = 0
    private var MagnitudePrevious = 0.0

    private lateinit var tv_stepsTaken : Button

    private var currentlongitude by Delegates.notNull<Double>()
    private var currentlatitude by Delegates.notNull<Double>()

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

        val LatLongB = LatLngBounds.Builder()

        // Add a marker in Sydney and move the camera
        val loc = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(loc).title(placename))

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    currentlatitude = location!!.latitude
                    currentlongitude = location!!.longitude
                    Log.d(".maps", currentlatitude.toString() + " " + currentlongitude.toString())

                    val currentlocation = LatLng(currentlatitude, currentlongitude)
                    mMap.addMarker(MarkerOptions().position(currentlocation).title("Current location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                    Log.d(".maps", currentlocation.toString())

                    val options = PolylineOptions()
                    options.color(Color.RED)
                    options.width(5f)

                    val url = getURL(currentlocation, loc)
                    var encoded = listOf<String>()

                    async {
                        val result = URL(url).readText()
                        uiThread {
                            val parser: Parser = Parser()
                            val stringBuilder: StringBuilder = StringBuilder(result)
                            val json: JsonObject = parser.parse(stringBuilder) as JsonObject

                            val routes = json.array<JsonObject>("routes")
                            val points = routes!!["legs"]["steps"][0] as JsonArray<JsonObject>
                            // For every element in the JsonArray, decode the polyline string and pass all points to a List
                            encoded = points.map { it.obj("polyline")?.string("points")!!  }
                            val polypts = points.flatMap { decodePoly(it.obj("polyline")?.string("points")!!)  }
                            // Add  points to polyline and bounds
                            options.add(currentlocation)
                            LatLongB.include(currentlocation)
                            for (point in polypts)  {
                                options.add(point)
                                LatLongB.include(point)
                            }
                            options.add(loc)
                            LatLongB.include(loc)
                            // build bounds
                            val bounds = LatLongB.build()
                            // add polyline to the map
                            mMap!!.addPolyline(options)
                            // show map with route centered

                            val zoomLevel = 12.0f //This goes up to 21
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoomLevel))
                        }

                    }

                    val url2 = getURLDistance(currentlocation, loc)
                    async {
                        val result = URL(url2).readText()
                        uiThread {
                            val parser: Parser = Parser()
                            val stringBuilder: StringBuilder = StringBuilder(result)
                            val json: JsonObject = parser.parse(stringBuilder) as JsonObject

                            val row = json.array<JsonObject>("rows")
                            val distance = row!!["elements"]["distance"]["text"][0] as String
                            val tokens = distance.split(" ");
                            stepToMake = (tokens[0].toInt() * 1.609 * 1000).toInt()
                            step_to_make.text =  "T.Steps: $stepToMake";

                        }
                    }


                }

        //mMap.setMyLocationEnabled(true);
    }

    private fun getURL(from : LatLng, to : LatLng) : String {
        val origin = "origin=" + from.latitude + "," + from.longitude
        val dest = "destination=" + to.latitude + "," + to.longitude
        val sensor = "key=AIzaSyDyzSbBxN2UIK-bGeJXFg8_mFp84IDrnYA"
        val params = "$origin&$dest&$sensor"
        return "https://maps.googleapis.com/maps/api/directions/json?$params"
    }

    private fun getURLDistance(from : LatLng, to : LatLng) : String {
        val origin = "origins=" + from.latitude + "," + from.longitude
        val dest = "destinations=" + to.latitude + "," + to.longitude
        val sensor = "key=AIzaSyDyzSbBxN2UIK-bGeJXFg8_mFp84IDrnYA"
        val params = "$origin&$dest&$sensor"
        return "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&$params"
    }

    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                    lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
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