package com.hkm.userhub.ui.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hkm.userhub.db.UserRepository
import com.hkm.userhub.entitiy.User
import com.hkm.userhub.tools.Event
import com.hkm.userhub.ui.home.HomeViewModel
import io.realm.Realm

class FavoriteViewModel(mApplication: Application) : AndroidViewModel(mApplication) {
    companion object {
        private var TAG = HomeViewModel::class.java.simpleName
    }

    private val realm = Realm.getDefaultInstance()
    private val userRepository = UserRepository(realm)
    private val statusMessage = MutableLiveData<Event<Int>>()
    private val listFavorite = MutableLiveData<ArrayList<User>>()

    val message: LiveData<Event<Int>>
        get() = statusMessage

    fun fetchDataFromRealm() {
        listFavorite.postValue(userRepository.getAllFavorite())
    }

    fun getListFavorite(): LiveData<ArrayList<User>> = listFavorite
}
