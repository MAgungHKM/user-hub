package com.hkm.userhub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream


class HomeFragment : Fragment() {
    private var listUser: ArrayList<User> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_users.setHasFixedSize(true)

        if (listUser.isEmpty())
            addDataToList()

        showUserList()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Home"
    }

    private fun addDataToList() {
        val jsonObj = JSONObject(loadJSONFromAsset()!!)
        val userJson = jsonObj.getJSONArray("users")
        for (i in 0 until userJson.length()) {
            val avatar = userJson.getJSONObject(i).getString("avatar").replace("@drawable/", "")
            val avatarId = resources.getIdentifier(avatar, "drawable", context?.packageName)
            val name = userJson.getJSONObject(i).getString("name")
            val username = userJson.getJSONObject(i).getString("username")
            val location = userJson.getJSONObject(i).getString("location")
            val repository = userJson.getJSONObject(i).getString("repository")
            val company = userJson.getJSONObject(i).getString("company")
            val follower = userJson.getJSONObject(i).getString("follower")
            val following = userJson.getJSONObject(i).getString("following")

            val user =
                User(avatarId, name, username, location, repository, company, follower, following)
            listUser.add(user)
        }
    }

    private fun loadJSONFromAsset(): String? {
        return try {
            val inputStream: InputStream = resources.openRawResource(R.raw.githubuser)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, charset("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun showUserList() {
        rv_users.layoutManager = LinearLayoutManager(this.context)
        val userAdapter = UserAdapter(listUser)
        rv_users.adapter = userAdapter

        userAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(user: User) {
                showSelectedUser(user)
            }
        })
    }

    private fun showSelectedUser(user: User) {
        val mDetailFragment = DetailFragment()

        val mBundle = Bundle()
        mBundle.putParcelable(DetailFragment.EXTRA_USER, user)

        mDetailFragment.arguments = mBundle

        val mFragmentManager = fragmentManager
        mFragmentManager?.beginTransaction()?.apply {
            replace(R.id.frame_container, mDetailFragment, DetailFragment::class.simpleName)
            addToBackStack(null)
            commit()
        }
    }
}