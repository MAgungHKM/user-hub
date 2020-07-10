package com.hkm.userhub.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hkm.userhub.R
import com.hkm.userhub.model.user.User
import com.hkm.userhub.ui.SectionsPagerAdapter
import com.hkm.userhub.ui.follow_repo.FollowRepoFragment
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : Fragment() {
    companion object {
        var EXTRA_USER = "extra_user"
    }
    private lateinit var detailViewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance((activity as AppCompatActivity).application)).get(DetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoading(true)

        if (arguments != null) {
            val username = arguments?.getParcelable<User>(EXTRA_USER)?.username.toString()

            detailViewModel.getUserDetail(username).observe(viewLifecycleOwner, Observer { user ->
                if(user.name == "null") {
                    (activity as AppCompatActivity).supportActionBar?.title = user.username
                    tv_name.text = user.username
                    tv_username.visibility = View.GONE
                } else {
                    (activity as AppCompatActivity).supportActionBar?.title = user.name
                    tv_name.text = user.name
                    tv_username.text = user.username
                }

                if (user.company != "null")
                    tv_company.text = user.company
                else
                    tv_company.visibility = View.GONE

                if (user.location != "null")
                    tv_location.text = user.location
                else
                    tv_location.visibility = View.GONE

                Glide.with(this)
                        .load(user.avatar)
                        .apply(RequestOptions().override(500, 500))
                        .into(img_avatar)

                setupSectionsPager(user.username)

                showLoading(false)
            })
        }
    }

    private fun setupSectionsPager(username: String) {
        val mBundle = Bundle()
        mBundle.putString(FollowRepoFragment.EXTRA_USER, username)

        val sectionsPagerAdapter = SectionsPagerAdapter(context!!, (activity as AppCompatActivity).supportFragmentManager, mBundle)
        sectionsPagerAdapter.notifyDataSetChanged()
        view_pager.adapter = sectionsPagerAdapter
        tab_layout.setupWithViewPager(view_pager, true)

        (activity as AppCompatActivity).supportActionBar?.elevation = 0f
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        requireActivity()
//            .onBackPressedDispatcher
//            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                }
//            })
//    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progress_bar.visibility = View.VISIBLE
            card_container.visibility = View.GONE
        } else {
            progress_bar.visibility = View.GONE
            card_container.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = ""
    }

    override fun onDetach() {
        super.onDetach()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.title = "Home"
    }
}