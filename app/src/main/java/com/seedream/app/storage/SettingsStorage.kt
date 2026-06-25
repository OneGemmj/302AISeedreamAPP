package com.seedream.app.storage

import android.content.Context
import android.content.SharedPreferences

class SettingsStorage(context: Context) {
    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun saveSetting(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getSetting(key: String, defaultValue: String): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    companion object {
        private const val PREFS = "seedream_settings"
    }
}
