package com.hkm.userhub.db

import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
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
            Log.d(TAG, "onSuccess: Data is saved successfully!")
        }, {
            // On Error
            Log.e(TAG, "onError: Error in saving data!")
        })
    }

    fun insert(values: ContentValues) {
        realm.executeTransactionAsync({
            val user =
                it.createObject(User::class.java, values.getAsString(DatabaseContract.COL_USERNAME))
            user.avatar = values.getAsString(DatabaseContract.COL_AVATAR)
            user.name = values.getAsString(DatabaseContract.COL_NAME)
            user.location = values.getAsString(DatabaseContract.COL_LOCATION)
            user.company = values.getAsString(DatabaseContract.COL_COMPANY)
            user.followers = values.getAsString(DatabaseContract.COL_FOLLOWERS)
        }, {
            // On Success
            Log.d(TAG, "onSuccess: Data is saved successfully!")
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

    fun queryAllUser(): Cursor {
        val columns = arrayOf(
            "_ID",
            DatabaseContract.COL_AVATAR,
            DatabaseContract.COL_NAME,
            DatabaseContract.COL_USERNAME,
            DatabaseContract.COL_LOCATION,
            DatabaseContract.COL_COMPANY,
            DatabaseContract.COL_FOLLOWERS
        )

        val results = realm.where(User::class.java).findAll()
        val cursor = MatrixCursor(columns)

        for (user in results) {
            val rowData = arrayOf(
                user.username,
                user.avatar,
                user.name,
                user.username,
                user.location,
                user.company,
                user.followers
            )
            cursor.addRow(rowData)
        }

        return cursor
    }

    fun getUser(username: String): User? =
        realm.where(User::class.java).equalTo("username", username).findFirst()

    fun deleteUser(username: String) {
        val user = realm.where(User::class.java).equalTo("username", username).findAll()
        realm.executeTransaction {
            if (user.deleteAllFromRealm())
                Log.d(TAG, "onSuccess: Data is deleted successfully!")
            else
                Log.e(TAG, "onError: Error in deleting data!")
        }
    }

    fun deleteAll() {
        val user = realm.where(User::class.java).findAll()
        realm.executeTransaction {
            if (user.deleteAllFromRealm())
                Log.d(TAG, "onSuccess: All Data is deleted successfully!")
            else
                Log.e(TAG, "onError: Error in deleting data!")
        }
    }
}