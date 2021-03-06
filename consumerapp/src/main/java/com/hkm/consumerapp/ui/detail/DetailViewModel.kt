package com.hkm.consumerapp.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.hkm.consumerapp.BuildConfig
import com.hkm.consumerapp.R
import com.hkm.consumerapp.entitiy.User
import com.hkm.consumerapp.tools.Event
import org.json.JSONException

class DetailViewModel(mApplication: Application) : AndroidViewModel(mApplication) {
    companion object {
        private const val TOKEN = BuildConfig.GITHUB_TOKEN
        private const val detailApi = "https://api.github.com/users/<USERNAME>"
        private var TAG = DetailViewModel::class.java.simpleName
    }

    private val user = MutableLiveData<User>()
    private val requestQueue = Volley.newRequestQueue(mApplication.applicationContext)
    private val statusMessage = MutableLiveData<Event<Int>>()

    val message: LiveData<Event<Int>>
        get() = statusMessage

    fun getUserDetail(username: String): LiveData<User> {
        val user = User()
        Log.d(TAG, "getUserDetail: Loading.....")
        val url = detailApi.replace("<USERNAME>", username)
        val request =
            object : JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
                try {
                    val avatar = response.getString("avatar_url")
                    user.avatar = avatar

                    val name = response.getString("name")
                    user.name = name

                    val login = response.getString("login")
                    user.username = login

                    val location = response.getString("location")
                    user.location = location

                    val company = response.getString("company")
                    user.company = company

                    val followersCount = response.getString("followers")
                    user.followers = followersCount

                    this.user.postValue(user)
                    Log.d(TAG, "getUserDetail: Success")
                } catch (e: JSONException) {
                    Log.d(TAG, "getUserDetail: Exception ${e.message.toString()}")
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
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Authorization"] = TOKEN
                    return headers
                }
            }
        requestQueue.add(request)

        return this.user
    }
}