package com.hkm.userhub.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.hkm.userhub.db.DatabaseContract
import com.hkm.userhub.db.UserRepository
import io.realm.Realm

class FavoriteProvider : ContentProvider() {
    companion object {
        private const val FAVORITE = 100
        private const val FAVORITE_USERNAME = 200
        private val mUriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    }

    init {
        mUriMatcher.addURI(DatabaseContract.AUTHORITY, DatabaseContract.TABLE_NAME, FAVORITE)

        mUriMatcher.addURI(
            DatabaseContract.AUTHORITY,
            "${DatabaseContract.TABLE_NAME}/*",
            FAVORITE_USERNAME
        )
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val realm = Realm.getDefaultInstance()
        val userRepository = UserRepository(realm)
        var cursor: Cursor

        realm.use {
            when (mUriMatcher.match(uri)) {
                FAVORITE -> {
                    cursor = userRepository.getAllFavorite()
                }
                else -> throw throw UnsupportedOperationException("Unknown Uri: $uri")
            }
        }

        cursor.setNotificationUri(context?.contentResolver, uri)

        return cursor
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var count = 0
        val realm = Realm.getDefaultInstance()
        val userRepository = UserRepository(realm)

        Log.e("FUUUUUUUUUUUUUUUUUUU", uri.toString())

        realm.use {
            when (mUriMatcher.match(uri)) {
                FAVORITE -> {
                    userRepository.deleteAllFavorite()
                    count++
                }
                FAVORITE_USERNAME -> {
                    val path = uri.path
                    val username = path?.substring(path.lastIndexOf('/') + 1).toString()
                    userRepository.deleteFavoriteByUsername(username)
                    count++
                }
                else -> throw IllegalArgumentException("Illegal delete Uri")
            }
        }

        if (count > 0)
            context?.contentResolver?.notifyChange(uri, null)

        return count
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val realm = Realm.getDefaultInstance()
        val userRepository = UserRepository(realm)
        var returnUri: Uri

        realm.use {
            when (FAVORITE) {
                mUriMatcher.match(uri) -> {
                    userRepository.insertFavorite(values as ContentValues)
                    returnUri =
                        ContentUris.withAppendedId(DatabaseContract.CONTENT_URI, '1'.toLong())
                }
                else -> throw UnsupportedOperationException("Unknown Uri: $uri")
            }
        }

        context?.contentResolver?.notifyChange(uri, null)

        return returnUri
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw UnsupportedOperationException("Unknown Uri: $uri")
    }
}
