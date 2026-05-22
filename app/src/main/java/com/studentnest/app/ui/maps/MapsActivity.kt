package com.studentnest.app.ui.maps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.studentnest.app.R
import com.studentnest.app.databinding.ActivityMapsBinding
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private lateinit var googleMap: GoogleMap
    private var listingLat: Double = 0.0
    private var listingLng: Double = 0.0
    private var listingTitle: String = ""

    private companion object {
        const val EARTH_RADIUS_KM = 6371.0
        const val WALK_SPEED_KMH = 5.0
        const val DRIVE_SPEED_KMH = 40.0
        const val UB_LAT = -24.6579
        const val UB_LNG = 25.9180
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listingLat = intent.getDoubleExtra("lat", -24.658)
        listingLng = intent.getDoubleExtra("lng", 25.912)
        listingTitle = intent.getStringExtra("title") ?: "Property"

        binding.toolbar.title = listingTitle
        binding.toolbar.setNavigationOnClickListener { finish() }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val listingLocation = LatLng(listingLat, listingLng)
        val ubLocation = LatLng(UB_LAT, UB_LNG)

        googleMap.addMarker(
            MarkerOptions().position(listingLocation).title(listingTitle)
                .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                    com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_RED
                ))
        )

        googleMap.addMarker(
            MarkerOptions().position(ubLocation).title("University of Botswana")
                .icon(com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(
                    com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_BLUE
                ))
        )

        googleMap.addPolyline(
            PolylineOptions()
                .add(listingLocation, ubLocation)
                .color(ContextCompat.getColor(this, R.color.navy_700))
                .width(8f)
        )

        val distance = calculateHaversineDistance(listingLat, listingLng, UB_LAT, UB_LNG)
        val walkTime = (distance / WALK_SPEED_KMH * 60).toInt()
        val driveTime = (distance / DRIVE_SPEED_KMH * 60).toInt()

        binding.tvDistance.text = String.format(
            "%.2f km | %d min walk | %d min drive",
            distance,
            walkTime,
            driveTime
        )

        val centerLat = (listingLat + UB_LAT) / 2
        val centerLng = (listingLng + UB_LNG) / 2
        val centerPoint = LatLng(centerLat, centerLng)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPoint, 13f))
    }

    private fun calculateHaversineDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }
}
