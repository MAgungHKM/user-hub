package com.hkm.userhub.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.hkm.userhub.R
import com.hkm.userhub.tools.ContentWrapper
import com.hkm.userhub.tools.OnMyFragmentListener
import com.hkm.userhub.tools.SharedPreferences
import com.hkm.userhub.ui.home.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnMyFragmentListener {
    companion object {
        private const val KEY_LANGUAGE = "key_language"
    }

    private var sharedPref: SharedPreferences? = null

    override fun attachBaseContext(newBase: Context) {
        sharedPref = SharedPreferences(newBase)
        val languageCode = sharedPref?.getData(KEY_LANGUAGE)
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

    override fun showAlertDialog(menuId: Int, tag: String) {
        var mAlertDialog: AlertDialog? = null
        val mBuilder: AlertDialog.Builder =
            AlertDialog.Builder(this@MainActivity, R.style.MyPopupMenu)
        when (menuId) {
            R.id.menu_language -> {
                mBuilder.setTitle(getString(R.string.choose_language))

                val languages = arrayOf(
                    getString(R.string.language_english),
                    getString(R.string.language_indonesian)
                )

                val checkedItem = when (sharedPref?.getData(KEY_LANGUAGE)) {
                    "en" -> 0
                    "in" -> 1
                    else -> 0
                }

                mBuilder.setSingleChoiceItems(languages, checkedItem) { _, which ->
                    when (which) {
                        0 -> sharedPref?.saveData(KEY_LANGUAGE, "en")
                        1 -> sharedPref?.saveData(KEY_LANGUAGE, "in")
                    }
                    mAlertDialog?.dismiss()
                    onRecreateActivity(tag)
                }
            }
        }
        mAlertDialog = mBuilder.create()
        mAlertDialog.setCanceledOnTouchOutside(false)
        mAlertDialog.show()
    }

    override fun onRecreateActivity(tag: String) {
        when (tag) {
            HomeFragment::class.java.simpleName -> {
                val restartIntent = intent
                this.finish()
                startActivity(restartIntent)
            }
            else -> this.recreate()
        }
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
