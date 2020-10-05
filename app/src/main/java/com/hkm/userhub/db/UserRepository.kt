package com.hkm.userhub.db

import android.content.ContentValues
import android.database.Cursor
import com.hkm.userhub.entitiy.User
import io.realm.Realm

class UserRepository(realm: Realm) {
    private val userDao = UserDao(realm)

    fun insertFavorite(values: ContentValues) = userDao.insert(values)

    fun getAllFavorite(): Cursor = userDao.queryAllUser()

    fun getFavoriteByUsername(username: String): User? = userDao.getUser(username)

    fun deleteFavoriteByUsername(username: String) = userDao.deleteUser(username)

    fun deleteAllFavorite() = userDao.deleteAll()
}