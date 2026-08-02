package com.hud.systemwindow

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private val clockHandler = Handler(Looper.getMainLooper())
    private lateinit var tvClock: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvWeather: TextView

    private val clockTicker = object : Runnable {
        override fun run() {
            val now = Date()
            tvClock.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(now)
            tvDate.text = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(now)
            clockHandler.postDelayed(this, 1000L)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvClock = findViewById(R.id.tvClock)
        tvDate = findViewById(R.id.tvDate)
        tvWeather = findViewById(R.id.tvWeather)

        findViewById<android.widget.ImageButton>(R.id.btnMinimize).setOnClickListener {
            moveTaskToBack(true)
        }
        findViewById<android.widget.ImageButton>(R.id.btnMaximize).setOnClickListener {
            // Already full screen on a phone; toggles immersive layout for a "maximize" feel.
            window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        findViewById<android.widget.ImageButton>(R.id.btnClose).setOnClickListener {
            finishAffinity()
        }

        findViewById<android.widget.LinearLayout>(R.id.navHome).setOnClickListener { }
        findViewById<android.widget.LinearLayout>(R.id.navTasks).setOnClickListener {
            startActivity(Intent(this, TasksActivity::class.java))
        }
        findViewById<android.widget.LinearLayout>(R.id.navFiles).setOnClickListener {
            startActivity(Intent(this, FilesActivity::class.java))
        }
        findViewById<android.widget.LinearLayout>(R.id.navTools).setOnClickListener {
            startActivity(Intent(this, ToolsActivity::class.java))
        }
        findViewById<android.widget.LinearLayout>(R.id.navSettings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        requestLocationAndWeather()
    }

    override fun onResume() {
        super.onResume()
        clockHandler.post(clockTicker)
        requestLocationAndWeather()
    }

    override fun onPause() {
        super.onPause()
        clockHandler.removeCallbacks(clockTicker)
    }

    private fun requestLocationAndWeather() {
        val apiKey = Prefs.getWeatherApiKey(this)
        if (apiKey.isBlank()) {
            tvWeather.text = "Weather: add API key in Settings"
            return
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            tvWeather.text = "Weather: location permission needed"
            return
        }
        val lm = getSystemService(LOCATION_SERVICE) as LocationManager
        val providers = lm.getProviders(true)
        var location: android.location.Location? = null
        for (provider in providers) {
            val loc = lm.getLastKnownLocation(provider) ?: continue
            if (location == null || loc.accuracy < location.accuracy) location = loc
        }
        if (location == null) {
            tvWeather.text = "Weather: waiting for location fix"
            return
        }
        tvWeather.text = "Weather: loading…"
        WeatherHelper.fetch(location.latitude, location.longitude, apiKey) { temp, desc ->
            tvWeather.text = if (temp != null && desc != null) {
                "${temp.toInt()}°C · $desc"
            } else {
                "Weather: unable to fetch (check API key / network)"
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) requestLocationAndWeather()
    }
}
