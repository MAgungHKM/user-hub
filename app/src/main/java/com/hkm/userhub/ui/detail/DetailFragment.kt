package com.hkm.userhub.ui.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hkm.userhub.R
import com.hkm.userhub.adapter.SectionsPagerAdapter
import com.hkm.userhub.tools.OnMyFragmentListener
import com.hkm.userhub.ui.follow_repo.FollowRepoFragment
import kotlinx.android.synthetic.main.fragment_detail.*

class DetailFragment : Fragment(), Toolbar.OnMenuItemClickListener {
    private lateinit var detailViewModel: DetailViewModel
    private var mOnMyFragmentListener: OnMyFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMyFragmentListener) {
            mOnMyFragmentListener = context
        } else {
            throw RuntimeException(
                "$context must implement OnFragmentInteractionListener"
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        detailViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance((activity as AppCompatActivity).application)
        ).get(DetailViewModel::class.java)
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

        detailViewModel.message.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(context, getString(it), Toast.LENGTH_SHORT).show()
            }
        })

        if (arguments != null) {
            val username = DetailFragmentArgs.fromBundle(arguments as Bundle).username

            detailViewModel.getUserDetail(username).observe(viewLifecycleOwner, { user ->
                if (user.name == "null") {
                    mOnMyFragmentListener?.onChangeToolbarTitle(user.username)
                    tv_name.text = user.username
                    tv_username.visibility = View.GONE
                } else {
                    mOnMyFragmentListener?.onChangeToolbarTitle(user.name)
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

                btn_favorite.setOnClickListener {
                    val anim = AnimationUtils.loadAnimation(it.context, R.anim.favorite_anim)
                    btn_favorite.startAnimation(anim)
                    if (!detailViewModel.isFavoriteExist(user.username)) {
                        detailViewModel.insertFavorite(user)
                    } else {
                        detailViewModel.deleteFavorite(user.username)
                        Toast.makeText(context, "User Exist", Toast.LENGTH_LONG).show()
                    }
                }

                setupSectionsPager(user.username)

                showLoading(false)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        mOnMyFragmentListener?.onChangeToolbarDisplayHome(true)
        mOnMyFragmentListener?.onOptionsMenuSelected(this)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_language -> {
                mOnMyFragmentListener?.showAlertDialog(
                    R.id.menu_language,
                    DetailFragment::class.java.simpleName
                )
                true
            }
            R.id.menu_home -> {
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
                true
            }
            else -> true
        }
    }

    private fun setupSectionsPager(username: String) {
        val mBundle = Bundle()
        mBundle.putString(FollowRepoFragment.EXTRA_USER, username)

        val sectionsPagerAdapter = SectionsPagerAdapter(
            context as Context,
            (activity as AppCompatActivity).supportFragmentManager,
            mBundle
        )
        sectionsPagerAdapter.notifyDataSetChanged()
        view_pager.adapter = sectionsPagerAdapter
        view_pager.offscreenPageLimit = 3
        tab_layout.setupWithViewPager(view_pager, true)

        mOnMyFragmentListener?.onChangeToolbarElevation(0f)
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progress_bar.visibility = View.VISIBLE
            card_container.visibility = View.GONE
        } else {
            progress_bar.visibility = View.GONE
            card_container.visibility = View.VISIBLE
        }
    }

    override fun onDetach() {
        super.onDetach()
        mOnMyFragmentListener = null
    }
}