package com.hud.systemwindow

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

/**
 * Intentionally minimal: no placeholder/fake tools are wired in until
 * specified. Real tools (battery, notes, etc.) get added here on request.
 */
class ToolsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tools)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }
}
