package com.hkm.consumerapp.tools

import androidx.appcompat.widget.Toolbar

interface OnMyFragmentListener {
    fun onRecreateActivity(tag: String)
    fun onChangeToolbarTitle(title: String?)
    fun onChangeToolbarDisplayHome(display: Boolean)
    fun onChangeToolbarElevation(elevation: Float)
    fun onOptionsMenuSelected(mListener: Toolbar.OnMenuItemClickListener)
    fun inflateOptionsMenu(menu: Int)
    fun setMenuVisibility(menu: Int, visible: Boolean)
    fun setGroupMenuVisibility(groupId: Int, visible: Boolean)
}