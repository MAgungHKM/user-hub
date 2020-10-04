package com.hkm.userhub.tools

import androidx.appcompat.widget.Toolbar
import com.hkm.userhub.ui.MainActivity

interface OnMyFragmentListener {
    fun onRecreateActivity(tag: String)
    fun onChangeToolbarTitle(title: String?)
    fun onChangeToolbarDisplayHome(display: Boolean)
    fun onChangeToolbarElevation(elevation: Float)
    fun onOptionsMenuSelected(mListener: Toolbar.OnMenuItemClickListener)
    fun inflateOptionsMenu(menu: Int)
    fun showAlertDialog(menuId: Int, tag: String)
    fun setMenuVisibility(menu: Int, visible: Boolean)
    fun setGroupMenuVisibility(groupId: Int, visible: Boolean)
    fun setOnAlertConfirmDialog(onAlertConfirmDialog: MainActivity.OnAlertConfirmDialog)
}