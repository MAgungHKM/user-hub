package com.hkm.consumerapp.ui.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hkm.consumerapp.db.DatabaseContract
import com.hkm.consumerapp.entitiy.User
import com.hkm.consumerapp.tools.Event
import com.hkm.consumerapp.tools.MappingHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FavoriteViewModel(private val mApplication: Application) : AndroidViewModel(mApplication) {

    private val statusMessage = MutableLiveData<Event<Int>>()
    private val listFavorite = MutableLiveData<ArrayList<User>>()

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
}
