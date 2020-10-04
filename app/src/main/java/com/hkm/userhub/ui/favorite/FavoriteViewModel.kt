package com.hkm.userhub.ui.favorite

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hkm.userhub.R
import com.hkm.userhub.db.UserRepository
import com.hkm.userhub.entitiy.User
import com.hkm.userhub.tools.Event
import com.hkm.userhub.ui.home.HomeViewModel
import io.realm.Realm

class FavoriteViewModel(mApplication: Application) : AndroidViewModel(mApplication) {
    companion object {
        private var TAG = HomeViewModel::class.java.simpleName
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

    private fun fetchDataFromRealm() = listFavorite.postValue(userRepository.getAllFavorite())

    fun getListFavorite(): LiveData<ArrayList<User>> {
        this.fetchDataFromRealm()
        return listFavorite
    }

    fun deleteFavorite(username: String) {
        userRepository.deleteFavoriteByUsername(username)
        statusMessage.value = Event(R.string.del_favorite)
    }

    fun deleteAllFavorite() {
        userRepository.deleteAllFavorite()
        statusMessage.value = Event(R.string.del_favorite_all)
    }

    override fun onCleared() {
        Log.d(TAG, "Delete realm instance")
        realm.close()
        super.onCleared()
    }
}
