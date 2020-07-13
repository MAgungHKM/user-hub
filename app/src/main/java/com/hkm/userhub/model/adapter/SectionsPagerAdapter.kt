package com.hkm.userhub.model.adapter

import android.content.Context
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.hkm.userhub.R
import com.hkm.userhub.ui.follow_repo.FollowRepoFragment

class SectionsPagerAdapter(
    private val mContext: Context,
    fm: FragmentManager,
    private val mBundle: Bundle?
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    @StringRes
    private val tabTitles =
        intArrayOf(R.string.tab_followers, R.string.tab_following, R.string.tab_repositories)

    override fun getItem(position: Int): Fragment {
        var mFollowRepoFragment = FollowRepoFragment()
        if (mBundle != null) {
            val username = mBundle.getString(FollowRepoFragment.EXTRA_USER) as String
            mFollowRepoFragment = FollowRepoFragment.newInstance(position + 1, username)
        }

        return mFollowRepoFragment
    }


    @Nullable
    override fun getPageTitle(position: Int): CharSequence? =
        mContext.resources.getString(tabTitles[position])

    override fun getCount(): Int = tabTitles.size
}