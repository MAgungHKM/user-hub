package com.hkm.userhub.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceFragmentCompat
import com.hkm.userhub.R
import com.hkm.userhub.tools.OnMyFragmentListener
import com.hkm.userhub.ui.MainActivity
import com.hkm.userhub.ui.settings.SettingsViewModel.Companion.KEY_LANGUAGE
import com.hkm.userhub.ui.settings.SettingsViewModel.Companion.KEY_REMINDER

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var settingsViewModel: SettingsViewModel

    private var mOnMyFragmentListener: OnMyFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMyFragmentListener) {
            mOnMyFragmentListener = context
        } else {
            throw RuntimeException(
                "$context must implement OnFragmentInteractionListener"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance((activity as MainActivity).application)
        ).get(SettingsViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel.message.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(context, getString(it), Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()

        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        mOnMyFragmentListener?.onChangeToolbarTitle(getString(R.string.menu_settings))
        mOnMyFragmentListener?.onChangeToolbarDisplayHome(true)
        mOnMyFragmentListener?.setMenuVisibility(R.id.menu_favorite, false)
        mOnMyFragmentListener?.setMenuVisibility(R.id.menu_home, false)
        mOnMyFragmentListener?.setMenuVisibility(R.id.menu_delete_all, false)
        mOnMyFragmentListener?.setMenuVisibility(R.id.menu_settings, false)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onDetach() {
        super.onDetach()
        mOnMyFragmentListener = null
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            KEY_LANGUAGE -> {
                settingsViewModel.saveData(
                    KEY_LANGUAGE,
                    sharedPreferences.getString(
                        KEY_LANGUAGE,
                        resources.getStringArray(R.array.language_values)[0]
                    ).toString()
                )

                mOnMyFragmentListener?.onRecreateActivity(SettingsFragment::class.java.simpleName)
            }
            KEY_REMINDER -> {
                val switch = sharedPreferences.getBoolean(KEY_REMINDER, false)

                settingsViewModel.saveBoolean(KEY_REMINDER, switch)

                if (switch)
                    settingsViewModel.enableReminder(
                        context as Context,
                        getString(R.string.reminder_title),
                        getString(R.string.reminder_message)
                    )
                else
                    settingsViewModel.disableReminder(context as Context)
            }
        }
    }
}