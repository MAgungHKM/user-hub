package com.hkm.userhub.db

import android.util.Log
import com.hkm.userhub.entitiy.User
import io.realm.Realm

class UserDao(private val realm: Realm) {
    companion object {
        private var TAG = UserDao::class.java.simpleName
    }

    fun insert(user: User) {
        realm.executeTransactionAsync({
            it.copyToRealm(user)
        }, {
            // On Success
            Log.e(TAG, "onSuccess: Data is saved successfully!")
        }, {
            // On Error
            Log.e(TAG, "onError: Error in saving data!")
        })
    }

    fun getAllUser(): ArrayList<User> {
        val results = realm.where(User::class.java).findAll()
        val temp = realm.copyFromRealm(results)
        val list: ArrayList<User> = ArrayList()
        list.addAll(temp)
        return list
    }

    fun getUser(username: String): User? =
        realm.where(User::class.java).equalTo("username", username).findFirst()

    fun deleteUser(username: String) {
        val user = realm.where(User::class.java).equalTo("username", username).findAll()
        realm.executeTransaction {
            if (user.deleteAllFromRealm())
                Log.e(TAG, "onSuccess: Data is deleted successfully!")
            else
                Log.e(TAG, "onError: Error in deleting data!")
        }
    }

    fun deleteAll() {
        val user = realm.where(User::class.java).findAll()
        realm.executeTransaction {
            if (user.deleteAllFromRealm())
                Log.e(TAG, "onSuccess: All Data is deleted successfully!")
            else
                Log.e(TAG, "onError: Error in deleting data!")
        }
    }
}