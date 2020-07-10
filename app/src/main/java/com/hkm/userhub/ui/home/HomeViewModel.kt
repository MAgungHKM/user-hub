package com.hkm.userhub.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.hkm.userhub.MainActivity
import com.hkm.userhub.MainActivity.*
import com.hkm.userhub.model.user.User
import org.json.JSONException


class HomeViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TOKEN = "token f6deb3e1082f8e6f1f1ac624fa724dc05e1df781"
        private var TAG = HomeViewModel::class.java.simpleName
    }

    private val listSearch = MutableLiveData<ArrayList<User>>()
    private val requestQueue = Volley.newRequestQueue(application.applicationContext)
    private var followersCount = ""

    fun searchUser(input: String, callBack: VolleyCallBack) {
        Log.d(TAG, "searchUser: Searching.....")
        val listUser = ArrayList<User>()
        val url = "https://api.github.com/search/users?q=$input"
        val request = object : JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
            try {
                val users = response.getJSONArray("items")
                for (i in 0 until users.length()) {
                    val user = User()
                    val avatar = users.getJSONObject(i).getString("avatar_url")
                    user.avatar = avatar

                    val username = users.getJSONObject(i).getString("login")
                    user.username = username

                    getFollowersCount(username, object : VolleyCallBack {
                        override fun onSuccess() {
                            user.followersCount = followersCount
                            callBack.onSuccess()
                        }
                    })

//                    val user = getUserDetail(users.getJSONObject(i).getString("login"))
                    if(users.length() > 0)
                        listUser.add(user)
                }

                listSearch.postValue(listUser)
                Log.d(TAG, "searchUser: Success")
            } catch (e: JSONException) {
                Log.d(TAG, "searchUser: Exception ${e.message.toString()}")
                listSearch.postValue(arrayListOf())
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "searchUser: Failed ${error.message.toString()}")
            listSearch.postValue(arrayListOf())
        })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = TOKEN
                return headers
            }
        }
        requestQueue.add(request)
    }

    fun getSearchResult() : LiveData<ArrayList<User>> = listSearch

    fun getFollowersCount(username: String, callBack: VolleyCallBack) {
        Log.d(TAG, "getFollowerCount: Counting.....")
        val url = "https://api.github.com/users/$username/followers"
        val request = object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
            try {
                followersCount = if (response.length() == 30) "30+" else response.length().toString()
                callBack.onSuccess()
                Log.d(TAG, "getFollowerCount: Success")
            } catch (e: JSONException) { Log.d(TAG, "getFollowerCount: Exception ${e.message}") }
        }, Response.ErrorListener { error -> Log.d(TAG, "getFollowerCount: Failed ${error.message}")})
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = TOKEN
                return headers
            }
        }
        requestQueue.add(request)
    }
}