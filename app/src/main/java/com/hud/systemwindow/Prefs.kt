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
}
