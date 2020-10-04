package com.hkm.userhub.db

import com.hkm.userhub.entitiy.User
import io.realm.Realm

class UserRepository(realm: Realm) {
    private val userDao = UserDao(realm)

    fun insertFavorite(user: User) = userDao.insert(user)

    fun getAllFavorite(): ArrayList<User> = userDao.getAllUser()

    fun getFavoriteByUsername(username: String): User? = userDao.getUser(username)

    fun deleteFavoriteByUsername(username: String) = userDao.deleteUser(username)

    fun deleteAllFavorite() = userDao.deleteAll()
}