package com.hkm.userhub.ui.favorite

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hkm.userhub.R
import com.hkm.userhub.db.DatabaseContract
import com.hkm.userhub.db.UserRepository
import com.hkm.userhub.entitiy.User
import com.hkm.userhub.tools.Event
import com.hkm.userhub.tools.MappingHelper
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteViewModel(private val mApplication: Application) : AndroidViewModel(mApplication) {
    companion object {
        private var TAG = FavoriteViewModel::class.java.simpleName
    }

    private val statusMessage = MutableLiveData<Event<Int>>()
    private val listFavorite = MutableLiveData<ArrayList<User>>()

    private var realm: Realm
    private var userRepository: UserRepository

    init {
        Log.d(TAG, "Initialize realm instance")
        realm = Realm.getDefaultInstance()
        userRepository = UserRepository(realm)
    }

    val message: LiveData<Event<Int>>
        get() = statusMessage

    private fun fetchDataFromRealm() {
        GlobalScope.launch(Dispatchers.Main) {
            val deferredFavorites = async(Dispatchers.IO) {
                val cursor = mApplication.contentResolver.query(
                    DatabaseContract.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
                )
                MappingHelper.mapCursorToArrayList(cursor)
            }
            val favorites = deferredFavorites.await()

            if (favorites.size > 0)
                listFavorite.postValue(favorites)
            else
                listFavorite.postValue(arrayListOf())
        }
    }

    fun getListFavorite(): LiveData<ArrayList<User>> {
        this.fetchDataFromRealm()
        return listFavorite
    }

    fun deleteFavorite(username: String) {
        val uri = Uri.parse("${DatabaseContract.CONTENT_URI}/$username")

        mApplication.applicationContext.contentResolver.delete(uri, null, null)
        statusMessage.value = Event(R.string.del_favorite, username)
    }

    fun deleteAllFavorite() {
        val uri = Uri.parse("${DatabaseContract.CONTENT_URI}")

        mApplication.applicationContext.contentResolver.delete(uri, null, null)
        statusMessage.value = Event(R.string.del_favorite_all)
    }

    override fun onCleared() {
        Log.d(TAG, "Delete realm instance")
        realm.close()
        super.onCleared()
    }
}
