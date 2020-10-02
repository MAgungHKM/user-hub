package com.hkm.userhub.tools

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPreferences(val context: Context) {
    private val prefsName = "shared_preferences"
    private val sharedPref: SharedPreferences =
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    fun saveData(key: String, value: String) {
        sharedPref.edit {
            putString(key, value)
            apply()
        }
    }

    fun getData(key: String): String? {
        return sharedPref.getString(key, null)
    }

    fun clear() {
        sharedPref.edit {
            clear()
            apply()
        }
    }

    fun removeData(key: String) {
        sharedPref.edit {
            remove(key)
            apply()
        }
    }
}