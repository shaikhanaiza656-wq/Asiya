package com.hud.systemwindow

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class FilesActivity : AppCompatActivity() {

    private lateinit var tvFileName: TextView
    private lateinit var tvFileMeta: TextView

    private val pickFileLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            if (uri != null) showFileInfo(uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_files)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        tvFileName = findViewById(R.id.tvFileName)
        tvFileMeta = findViewById(R.id.tvFileMeta)

        findViewById<Button>(R.id.btnPickFile).setOnClickListener {
            pickFileLauncher.launch(arrayOf("*/*"))
        }
    }

    /** Reads real metadata (name, size) for the picked document via ContentResolver — no fake values. */
    private fun showFileInfo(uri: Uri) {
        var name = uri.lastPathSegment ?: "unknown"
        var size = -1L
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                if (nameIdx >= 0) name = cursor.getString(nameIdx)
                if (sizeIdx >= 0) size = cursor.getLong(sizeIdx)
            }
        }
        tvFileName.text = name
        tvFileMeta.text = if (size >= 0) "${size / 1024} KB · $uri" else uri.toString()
    }
}
