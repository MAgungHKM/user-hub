package com.hkm.userhub.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.hkm.userhub.BuildConfig
import com.hkm.userhub.R
import com.hkm.userhub.entitiy.User
import com.hkm.userhub.tools.Event
import com.hkm.userhub.ui.MainActivity.VolleyCallBack
import org.json.JSONException


class HomeViewModel(mApplication: Application) : AndroidViewModel(mApplication) {
    companion object {
        private const val TOKEN = BuildConfig.GITHUB_TOKEN
        private const val searchApi = "https://api.github.com/search/users?q=<SEARCH_KEY>"
        private const val followersApi = "https://api.github.com/users/<USERNAME>/followers"
        private var TAG = HomeViewModel::class.java.simpleName
    }

    private val listSearch = MutableLiveData<ArrayList<User>>()
    private val requestQueue = Volley.newRequestQueue(mApplication.applicationContext)
    private var followersCount = ""
    private val statusMessage = MutableLiveData<Event<Int>>()

    val message: LiveData<Event<Int>>
        get() = statusMessage


    fun searchUser(input: String, callBack: VolleyCallBack) {
        Log.d(TAG, "searchUser: Searching.....")
        val listUser = ArrayList<User>()
        val url = searchApi.replace("<SEARCH_KEY>", input)
        val request =
            object : JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
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
                        if (users.length() > 0)
                            listUser.add(user)
                    }

                    listSearch.postValue(listUser)
                    Log.d(TAG, "searchUser: Success")
                } catch (e: JSONException) {
                    Log.d(TAG, "searchUser: Exception ${e.message.toString()}")
                    listSearch.postValue(arrayListOf())
                }
            }, Response.ErrorListener { error ->
                when (error) {
                    is NetworkError -> statusMessage.value = Event(R.string.no_internet)
                    is ServerError -> statusMessage.value = Event(R.string.no_server)
                    is AuthFailureError -> statusMessage.value = Event(R.string.no_internet)
                    is ParseError -> statusMessage.value = Event(R.string.no_parsing)
                    is NoConnectionError -> statusMessage.value = Event(R.string.no_internet)
                    is TimeoutError -> statusMessage.value = Event(R.string.no_timeout)
                }
                listSearch.postValue(arrayListOf())
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = TOKEN
                    return headers
                }
            }
        requestQueue.add(request)
    }

    fun getSearchResult(): LiveData<ArrayList<User>> = listSearch

    fun getFollowersCount(username: String, callBack: VolleyCallBack) {
        Log.d(TAG, "getFollowerCount: Counting.....")
        val url = followersApi.replace("<USERNAME>", username)
        val request = object : JsonArrayRequest(
            Method.GET,
            url,
            null,
            Response.Listener { response ->
                try {
                    followersCount =
                        if (response.length() == 30) "30+" else response.length().toString()
                    callBack.onSuccess()
                    Log.d(TAG, "getFollowerCount: Success")
                } catch (e: JSONException) {
                    Log.d(TAG, "getFollowerCount: Exception ${e.message}")
                }
            },
            Response.ErrorListener { error ->
                Log.d(
                    TAG,
                    "getFollowerCount: Failed ${error.message}"
                )
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = TOKEN
                return headers
            }
        }
        requestQueue.add(request)
    }
}