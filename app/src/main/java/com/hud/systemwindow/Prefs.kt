package com.hud.systemwindow

import android.content.Context

/**
 * Thin real wrapper around SharedPreferences. Used to persist the
 * user's OpenWeatherMap API key and the task list — no mock/in-memory
 * fake storage, everything survives app restarts.
 */
object Prefs {
    private const val FILE = "system_hud_prefs"
    private const val KEY_WEATHER_API_KEY = "weather_api_key"
    private const val KEY_TASKS = "tasks_json"

    private fun prefs(context: Context) =
        context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun getWeatherApiKey(context: Context): String =
        prefs(context).getString(KEY_WEATHER_API_KEY, "") ?: ""

    fun setWeatherApiKey(context: Context, key: String) {
        prefs(context).edit().putString(KEY_WEATHER_API_KEY, key).apply()
    }

    fun getTasksJson(context: Context): String =
        prefs(context).getString(KEY_TASKS, "[]") ?: "[]"

    fun setTasksJson(context: Context, json: String) {
        prefs(context).edit().putString(KEY_TASKS, json).apply()
    }

    /** Persisted drag offset (in px) for a movable HUD element, keyed by its own id string. */
    fun getDragOffset(context: Context, key: String): Pair<Float, Float> {
        val p = prefs(context)
        val x = p.getFloat(key + "_x", 0f)
        val y = p.getFloat(key + "_y", 0f)
        return x to y
    }

    fun setDragOffset(context: Context, key: String, x: Float, y: Float) {
        prefs(context).edit().putFloat(key + "_x", x).putFloat(key + "_y", y).apply()
    }
}
