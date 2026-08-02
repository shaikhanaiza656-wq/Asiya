package com.hud.systemwindow

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val etApiKey = findViewById<EditText>(R.id.etApiKey)
        etApiKey.setText(Prefs.getWeatherApiKey(this))

        findViewById<Button>(R.id.btnSaveKey).setOnClickListener {
            Prefs.setWeatherApiKey(this, etApiKey.text.toString().trim())
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }
    }
}
