package com.hkm.userhub.tools

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class NavigationHelper {
    companion object {
        fun getCurrentTopFragment(mFragmentManager: FragmentManager): Fragment? {
            val stackCount = mFragmentManager.backStackEntryCount

            if (stackCount > 0) {
                val backEntry = mFragmentManager.getBackStackEntryAt(stackCount - 1)
                return mFragmentManager.findFragmentByTag(backEntry.name)
            } else {
                val listFragment = mFragmentManager.fragments
                if (listFragment.size > 0) {
                    for (fragment in listFragment) {
                        if (fragment != null && !fragment.isHidden)
                            return fragment
                    }
                }
            }
            return null
        }
    }
}