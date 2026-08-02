package com.hud.systemwindow

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Real weather fetch against the OpenWeatherMap "current weather" REST API.
 * Requires the user's own API key (stored via Prefs, entered in Settings).
 * No mock JSON, no placeholder numbers — if the key or network call fails,
 * the callback receives null and the caller must show that state honestly.
 */
object WeatherHelper {

    fun fetch(lat: Double, lon: Double, apiKey: String, callback: (temp: Double?, desc: String?) -> Unit) {
        if (apiKey.isBlank()) {
            callback(null, null)
            return
        }
        Thread {
            var result: Pair<Double, String>? = null
            try {
                val url = URL(
                    "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=metric&appid=$apiKey"
                )
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 8000
                conn.readTimeout = 8000
                val code = conn.responseCode
                if (code == 200) {
                    val body = conn.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(body)
                    val temp = json.getJSONObject("main").getDouble("temp")
                    val desc = json.getJSONArray("weather").getJSONObject(0).getString("description")
                    result = temp to desc
                }
                conn.disconnect()
            } catch (e: Exception) {
                result = null
            }
            Handler(Looper.getMainLooper()).post {
                if (result != null) callback(result.first, result.second) else callback(null, null)
            }
        }.start()
    }
}
