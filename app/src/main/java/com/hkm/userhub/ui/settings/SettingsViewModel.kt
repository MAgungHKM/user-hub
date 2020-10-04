package com.hkm.userhub.ui.settings

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hkm.userhub.R
import com.hkm.userhub.receiver.ReminderReceiver
import com.hkm.userhub.tools.Event
import com.hkm.userhub.tools.SharedPreferences

class SettingsViewModel(mApplication: Application) : AndroidViewModel(mApplication) {
    companion object {
        private var TAG = SettingsViewModel::class.java.simpleName
        const val KEY_LANGUAGE = "language"
        const val KEY_REMINDER = "reminder"
    }

    private val sharedPreferences = SharedPreferences(mApplication.applicationContext)
    private val reminderReceiver = ReminderReceiver()
    private val statusMessage = MutableLiveData<Event<Int>>()

    val message: LiveData<Event<Int>>
        get() = statusMessage

    fun saveData(key: String, value: String) {
        sharedPreferences.saveData(key, value)
        Log.d(TAG, "Saving value=$value to key=$key")
    }

    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.saveBoolean(key, value)
        Log.d(TAG, "Saving value=$value to key=$key")
    }

    fun enableReminder(context: Context, title: String, message: String) {
        reminderReceiver.enableReminder(context, title, message)
        statusMessage.value = Event(R.string.reminder_on)
        Log.d(TAG, "Daily reminder enabled")
    }

    fun disableRemind(context: Context) {
        reminderReceiver.disableReminder(context)
        statusMessage.value = Event(R.string.reminder_off)
        Log.d(TAG, "Daily reminder disabled")
    }
}