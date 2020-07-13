package com.hkm.userhub.tools

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(val context: Context) {
    private val prefsName = "shared_preferences"
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    fun saveData(key: String, value: String) {
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getData(key: String): String? {
        return sharedPref.getString(key, null)
    }

    fun clear() {
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    fun removeData(key: String) {
        val editor = sharedPref.edit()
        editor.remove(key)
        editor.apply()
    }
}