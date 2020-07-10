package com.hkm.userhub.ui.follow_repo

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.hkm.userhub.MainActivity.VolleyCallBack
import com.hkm.userhub.R
import com.hkm.userhub.model.repo.Repo
import com.hkm.userhub.model.user.User
import com.hkm.userhub.ui.detail.DetailViewModel
import org.json.JSONException

class FollowRepoViewModel(private val mApplication: Application) : AndroidViewModel(mApplication) {
    companion object {
        private const val TOKEN = "token f6deb3e1082f8e6f1f1ac624fa724dc05e1df781"
        private var TAG = DetailViewModel::class.java.simpleName
    }
    private val listFollowers = MutableLiveData<ArrayList<User>>()
    private val listFollowing = MutableLiveData<ArrayList<User>>()
    private val listRepositories = MutableLiveData<ArrayList<Repo>>()
    private val requestQueue = Volley.newRequestQueue(mApplication.applicationContext)
    private var followersCount = ""

    fun setListOfFollowers(username: String, callBack: VolleyCallBack) {
        Log.d(TAG, "getListOfFollowers: Obtaining.....")
        val listFollowers = ArrayList<User>()
        val url = "https://api.github.com/users/$username/followers"
        val request = object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
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

//                    val user = getUserDetail(users.getJSONObject(i).getString("login"))
                    if(response.length() > 0)
                        listFollowers.add(user)
                }

                this.listFollowers.postValue(listFollowers)
                Log.d(TAG, "getListOfFollowers: Success")
            } catch (e: JSONException) {
                Log.d(TAG, "getListOfFollowers: Exception ${e.message.toString()}")
                this.listFollowers.postValue(arrayListOf())
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "getListOfFollowers: Failed ${error.message.toString()}")
            this.listFollowers.postValue(arrayListOf())
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

    fun getListOfFollowers() : LiveData<ArrayList<User>> = listFollowers

    fun setListOfFollowing(username: String, callBack: VolleyCallBack) {
        Log.d(TAG, "getListOfFollowing: Obtaining.....")
        val listFollowing = ArrayList<User>()
        val url = "https://api.github.com/users/$username/following"
        val request = object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
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

//                    val user = getUserDetail(users.getJSONObject(i).getString("login"))
                    if(response.length() > 0)
                        listFollowing.add(user)
                }

                this.listFollowing.postValue(listFollowing)
                Log.d(TAG, "getListOfFollowing: Success")
            } catch (e: JSONException) {
                Log.d(TAG, "getListOfFollowing: Exception ${e.message.toString()}")
                this.listFollowing.postValue(arrayListOf())
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "getListOfFollowing: Failed ${error.message.toString()}")
            this.listFollowing.postValue(arrayListOf())
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

    fun getListOfFollowing() : LiveData<ArrayList<User>> = listFollowing

    fun setListOfRepositories(username: String) {
        Log.d(TAG, "getListOfRepositories: Obtaining.....")
        val listRepositories = ArrayList<Repo>()
        val url = "https://api.github.com/users/$username/repos"
        val request = object : JsonArrayRequest(Method.GET, url, null, Response.Listener { response ->
            try {
                for (i in 0 until response.length()) {
                    val repo = Repo()
                    val name = response.getJSONObject(i).getString("name")
                    repo.name = name

                    val description = response.getJSONObject(i).getString("description")
                    repo.description =
                        if (description != "null") description else mApplication.applicationContext
                            .getString(R.string.description_not_found)

                    val stargazers = response.getJSONObject(i).getString("stargazers_count")
                    repo.stargazers = stargazers

                    val repoLink = response.getJSONObject(i).getString("html_url")
                    repo.repoLink = repoLink

                    if(response.length() > 0)
                        listRepositories.add(repo)
                }

                this.listRepositories.postValue(listRepositories)
                Log.d(TAG, "getListOfRepositories: Success")
            } catch (e: JSONException) {
                Log.d(TAG, "getListOfRepositories: Exception ${e.message.toString()}")
                this.listRepositories.postValue(arrayListOf())
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "getListOfRepositories: Failed ${error.message.toString()}")
            this.listRepositories.postValue(arrayListOf())
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

    fun getListOfRepositories() : LiveData<ArrayList<Repo>> = listRepositories

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