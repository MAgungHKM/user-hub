package com.hkm.userhub.tools

import android.database.Cursor
import com.hkm.userhub.db.DatabaseContract
import com.hkm.userhub.entitiy.User

object MappingHelper {
    fun mapCursorToArrayList(userCursor: Cursor?): ArrayList<User> {
        val userList = ArrayList<User>()
        userCursor?.apply {
            while (moveToNext()) {
                val avatar = getString(getColumnIndexOrThrow(DatabaseContract.COL_AVATAR))
                val name = getString(getColumnIndexOrThrow(DatabaseContract.COL_NAME))
                val username = getString(getColumnIndexOrThrow(DatabaseContract.COL_USERNAME))
                val location = getString(getColumnIndexOrThrow(DatabaseContract.COL_LOCATION))
                val company = getString(getColumnIndexOrThrow(DatabaseContract.COL_COMPANY))
                val followers = getString(getColumnIndexOrThrow(DatabaseContract.COL_FOLLOWERS))

                userList.add(User(avatar, name, username, location, company, followers))
            }
        }
        return userList
    }
}