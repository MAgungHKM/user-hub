package com.hkm.userhub.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.hkm.userhub.model.user.User
import org.json.JSONException

class DetailViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TOKEN = "token f6deb3e1082f8e6f1f1ac624fa724dc05e1df781"
        private var TAG = DetailViewModel::class.java.simpleName
    }
    private val user = MutableLiveData<User>()
    private val requestQueue = Volley.newRequestQueue(application.applicationContext)

    fun getUserDetail(username: String) : LiveData<User> {
        val user = User()
        Log.d(TAG, "getUserDetail: Loading.....")
        val url = "https://api.github.com/users/$username"
        val request = object : JsonObjectRequest(Method.GET, url, null, Response.Listener { response ->
            try{
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

                this.user.postValue(user)
                Log.d(TAG, "getUserDetail: Success")
            } catch (e: JSONException) {
                Log.d(TAG, "getUserDetail: Exception ${e.message.toString()}")
            }
        }, Response.ErrorListener { error ->
            Log.d(TAG, "getUserDetail: Failure ${error.message.toString()}")
        })
        {
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