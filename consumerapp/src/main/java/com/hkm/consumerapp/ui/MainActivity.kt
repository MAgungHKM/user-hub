package com.hkm.consumerapp.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.hkm.consumerapp.R
import com.hkm.consumerapp.tools.ContentWrapper
import com.hkm.consumerapp.tools.OnMyFragmentListener
import com.hkm.consumerapp.tools.SharedPreferences
import com.hkm.consumerapp.ui.settings.SettingsViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnMyFragmentListener {
    private lateinit var sharedPref: SharedPreferences

    override fun attachBaseContext(newBase: Context) {
        sharedPref = SharedPreferences(newBase)
        val languageCode = sharedPref.getData(SettingsViewModel.KEY_LANGUAGE)
        val mContext = ContentWrapper.changeLang(newBase, languageCode.toString())

        super.attachBaseContext(mContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState)
        }

        toolbar.inflateMenu(R.menu.main_menu)
    }

    override fun onRecreateActivity(tag: String) {
        this.recreate()
    }

    override fun onChangeToolbarTitle(title: String?) {
        toolbar.title = title
    }

    override fun onChangeToolbarDisplayHome(display: Boolean) {
        if (display) {
            toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back_white)
            toolbar.setNavigationOnClickListener { onBackPressed() }
        } else {
            toolbar.navigationIcon = null
            toolbar.setNavigationOnClickListener(null)
        }
    }

    override fun onChangeToolbarElevation(elevation: Float) {
        toolbar.elevation = elevation
    }

    override fun inflateOptionsMenu(menu: Int) = toolbar.inflateMenu(menu)

    override fun onOptionsMenuSelected(mListener: Toolbar.OnMenuItemClickListener) =
        toolbar.setOnMenuItemClickListener(mListener)

    override fun setMenuVisibility(menu: Int, visible: Boolean) {
        toolbar.menu.findItem(menu).isVisible = visible
    }

    override fun setGroupMenuVisibility(groupId: Int, visible: Boolean) {
        toolbar.menu.setGroupVisible(groupId, visible)
    }

    interface VolleyCallBack {
        fun onSuccess()
    }
}
