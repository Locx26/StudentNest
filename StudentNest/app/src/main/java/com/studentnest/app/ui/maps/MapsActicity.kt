package com.studentnest.app.ui.maps

import android.graphics.Color
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.studentnest.app.R
import com.studentnest.app.databinding.ActivityMapsBinding
import kotlin.math.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private var googleMap: GoogleMap? = null

    // Default data from Intent
    private var listingLat: Double = 0.0
    private var listingLng: Double = 0.0
    private var listingTitle: String = ""

    // Reference Point: University of Botswana (Main Campus)
    private val UB_LAT = -24.6579
    private val UB_LNG = 25.9180

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialize View Binding
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Setup Toolbar with Back Button
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false) // We use our custom tvMapTitle

        // 3. Extract Intent Data
        listingLat = intent.getDoubleExtra("lat", -24.65)
        listingLng = intent.getDoubleExtra("lng", 25.91)
        listingTitle = intent.getStringExtra("title") ?: "Property Location"
        binding.tvMapTitle.text = listingTitle

        // 4. Initialize Map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val listingPos = LatLng(listingLat, listingLng)
        val ubPos = LatLng(UB_LAT, UB_LNG)

        // 5. Map Styling & UI Settings
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isMapToolbarEnabled = true

        // Push the Google logo and zoom buttons UP so the floating card doesn't cover them
        map.setPadding(0, 0, 0, 280)

        // 6. Add Markers
        // Listing Marker (Red)
        map.addMarker(MarkerOptions()
            .position(listingPos)
            .title(listingTitle)
            .snippet("Target Property")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))

        // University Marker (Blue)
        map.addMarker(MarkerOptions()
            .position(ubPos)
            .title("University of Botswana")
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)))

        // 7. Draw a professional path (Polyline) between points
        map.addPolyline(PolylineOptions()
            .add(ubPos, listingPos)
            .width(10f)
            .color(Color.parseColor("#1A237E")) // Match brand color
            .jointType(JointType.ROUND)
            .geodesic(true))

        // 8. Calculate Distance and Update UI
        val distance = calculateHaversine(UB_LAT, UB_LNG, listingLat, listingLng)
        updateDistanceUI(distance)

        // 9. Animate Camera to fit both markers with padding
        val bounds = LatLngBounds.Builder()
            .include(ubPos)
            .include(listingPos)
            .build()

        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200))
    }

    private fun updateDistanceUI(distance: Double) {
        // Estimates: 5km/h walking, 40km/h driving
        val walkTime = (distance / 5.0 * 60).toInt()
        val driveTime = (distance / 40.0 * 60).toInt()

        binding.tvDistance.text = String.format(
            "%.2f km from Campus | %d min walk | %d min drive",
            distance, walkTime, driveTime
        )
    }

    /**
     * Calculates distance between two points in Kilometers
     */
    private fun calculateHaversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth's radius
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    // Handle Toolbar Back Click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}