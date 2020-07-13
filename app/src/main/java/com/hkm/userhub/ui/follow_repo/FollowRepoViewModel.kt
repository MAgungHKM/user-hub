package com.hkm.userhub.ui.follow_repo

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.hkm.userhub.MainActivity.VolleyCallBack
import com.hkm.userhub.R
import com.hkm.userhub.model.Repo
import com.hkm.userhub.model.User
import com.hkm.userhub.tools.Event
import com.hkm.userhub.ui.detail.DetailViewModel
import org.json.JSONException

class FollowRepoViewModel(private val mApplication: Application) : AndroidViewModel(mApplication) {
    companion object {
        private const val TOKEN = "token f5a17709f126ba686a2404a2c74dde99cad945e6"
        private var TAG = DetailViewModel::class.java.simpleName
    }

    private val listFollowers = MutableLiveData<ArrayList<User>>()
    private val listFollowing = MutableLiveData<ArrayList<User>>()
    private val listRepositories = MutableLiveData<ArrayList<Repo>>()
    private val requestQueue = Volley.newRequestQueue(mApplication.applicationContext)
    private var followersCount = ""
    private val statusMessage = MutableLiveData<Event<Int>>()

    val message: LiveData<Event<Int>>
        get() = statusMessage

    fun setListOfFollowers(username: String, callBack: VolleyCallBack) {
        Log.d(TAG, "getListOfFollowers: Obtaining.....")
        val listFollowers = ArrayList<User>()
        val url = "https://api.github.com/users/$username/followers"
        val request =
            object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
                try {
                    for (i in 0 until response.length()) {
                        val user = User()
                        val avatar = response.getJSONObject(i).getString("avatar_url")
                        user.avatar = avatar

                        val login = response.getJSONObject(i).getString("login")
                        user.username = login

                        getFollowersCount(login, object : VolleyCallBack {
                            override fun onSuccess() {
                                user.followersCount = followersCount
                                callBack.onSuccess()
                            }
                        })

                        if (response.length() > 0)
                            listFollowers.add(user)
                    }

                    this.listFollowers.postValue(listFollowers)
                    Log.d(TAG, "getListOfFollowers: Success")
                } catch (e: JSONException) {
                    Log.d(TAG, "getListOfFollowers: Exception ${e.message.toString()}")
                    this.listFollowers.postValue(arrayListOf())
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
                this.listFollowers.postValue(arrayListOf())
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = TOKEN
                    return headers
                }
            }
        requestQueue.add(request)
    }

    fun getListOfFollowers(): LiveData<ArrayList<User>> = listFollowers

    fun setListOfFollowing(username: String, callBack: VolleyCallBack) {
        Log.d(TAG, "getListOfFollowing: Obtaining.....")
        val listFollowing = ArrayList<User>()
        val url = "https://api.github.com/users/$username/following"
        val request =
            object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
                try {
                    for (i in 0 until response.length()) {
                        val user = User()
                        val avatar = response.getJSONObject(i).getString("avatar_url")
                        user.avatar = avatar

                        val login = response.getJSONObject(i).getString("login")
                        user.username = login

                        getFollowersCount(login, object : VolleyCallBack {
                            override fun onSuccess() {
                                user.followersCount = followersCount
                                callBack.onSuccess()
                            }
                        })

                        if (response.length() > 0)
                            listFollowing.add(user)
                    }

                    this.listFollowing.postValue(listFollowing)
                    Log.d(TAG, "getListOfFollowing: Success")
                } catch (e: JSONException) {
                    Log.d(TAG, "getListOfFollowing: Exception ${e.message.toString()}")
                    this.listFollowing.postValue(arrayListOf())
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
                this.listFollowing.postValue(arrayListOf())
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = TOKEN
                    return headers
                }
            }
        requestQueue.add(request)
    }

    fun getListOfFollowing(): LiveData<ArrayList<User>> = listFollowing

    fun setListOfRepositories(username: String) {
        Log.d(TAG, "getListOfRepositories: Obtaining.....")
        val listRepositories = ArrayList<Repo>()
        val url = "https://api.github.com/users/$username/repos"
        val request =
            object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
                try {
                    for (i in 0 until response.length()) {
                        val repo = Repo()
                        val name = response.getJSONObject(i).getString("name")
                        repo.name = name

                        val description = response.getJSONObject(i).getString("description")
                        repo.description = description

                        val stargazers = response.getJSONObject(i).getString("stargazers_count")
                        repo.stargazers = stargazers

                        val repoLink = response.getJSONObject(i).getString("html_url")
                        repo.repoLink = repoLink

                        if (response.length() > 0)
                            listRepositories.add(repo)
                    }

                    this.listRepositories.postValue(listRepositories)
                    Log.d(TAG, "getListOfRepositories: Success")
                } catch (e: JSONException) {
                    Log.d(TAG, "getListOfRepositories: Exception ${e.message.toString()}")
                    this.listRepositories.postValue(arrayListOf())
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
                this.listRepositories.postValue(arrayListOf())
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = TOKEN
                    return headers
                }
            }
        requestQueue.add(request)
    }

    fun getListOfRepositories(): LiveData<ArrayList<Repo>> = listRepositories

    fun getFollowersCount(username: String, callBack: VolleyCallBack) {
        Log.d(TAG, "getFollowerCount: Counting.....")
        val url = "https://api.github.com/users/$username/followers"
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