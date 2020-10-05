package com.hkm.userhub.db

import android.net.Uri

class DatabaseContract {
    companion object {
        const val AUTHORITY = "com.hkm.userhub.provider.FavoriteProvider"
        private const val SCHEME = "content"

        const val TABLE_NAME = "user"
        const val COL_AVATAR = "avatar"
        const val COL_NAME = "name"
        const val COL_USERNAME = "username"
        const val COL_LOCATION = "location"
        const val COL_COMPANY = "company"
        const val COL_FOLLOWERS = "followers"

        val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
            .authority(AUTHORITY)
            .appendPath(TABLE_NAME)
            .build()
    }
}