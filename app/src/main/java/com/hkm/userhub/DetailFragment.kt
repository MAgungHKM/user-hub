package com.hkm.userhub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_detail.*


class DetailFragment : Fragment() {
    companion object {
        var EXTRA_USER = "extra_user"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)

        if (arguments != null) {
            val user = arguments?.getParcelable<User>(EXTRA_USER) as User
            tv_name.text = user.name
            tv_username.text = user.username
            tv_company.text = user.company
            tv_location.text = user.location
            tv_followers.text = user.followers
            tv_following.text = user.following
            tv_repo.text = user.repository

            Glide.with(this)
                .load(user.avatar)
                .apply(RequestOptions().override(500, 500))
                .into(img_avatar)
        }

        (activity as AppCompatActivity).supportActionBar?.title = tv_name.text
    }
}