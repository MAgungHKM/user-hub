package com.hkm.userhub.tools

import androidx.appcompat.widget.Toolbar

interface OnMyFragmentListener {
    fun onRecreateActivity(tag: String)
    fun onChangeToolbarTitle(title: String?)
    fun onChangeToolbarDisplayHome(display: Boolean)
    fun onChangeToolbarElevation(elevation: Float)
    fun onOptionsMenuSelected(mListener: Toolbar.OnMenuItemClickListener)
    fun inflateOptionsMenu(menu: Int)
    fun showAlertDialog(menuId: Int, tag: String)
    fun setMenuVisibility(menu: Int, visible: Boolean)
}