package com.hkm.userhub.ui.follow_repo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hkm.userhub.MainActivity.VolleyCallBack
import com.hkm.userhub.R
import com.hkm.userhub.model.repo.Repo
import com.hkm.userhub.model.repo.RepoAdapter
import com.hkm.userhub.model.user.User
import com.hkm.userhub.model.user.UserAdapter
import com.hkm.userhub.ui.ItemSnaperHelper
import com.hkm.userhub.ui.detail.DetailFragment
import kotlinx.android.synthetic.main.fragment_follow_repo.*
import kotlinx.android.synthetic.main.fragment_follow_repo.progress_bar
import kotlinx.android.synthetic.main.fragment_follow_repo.tv_not_found


class FollowRepoFragment : Fragment() {
    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        var EXTRA_USER = "extra_user"

        fun newInstance(index: Int, username: String): FollowRepoFragment {
            val mFollowRepoFragment = FollowRepoFragment()
            val mBundle = Bundle()
            mBundle.putInt(ARG_SECTION_NUMBER, index)
            mBundle.putString(EXTRA_USER, username)
            mFollowRepoFragment.arguments = mBundle
            return mFollowRepoFragment
        }
    }

    private lateinit var followersAdapter: UserAdapter
    private lateinit var followingAdapter: UserAdapter
    private lateinit var repositoriesAdapter: RepoAdapter
    private lateinit var followRepoViewModel: FollowRepoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        followRepoViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance((activity as AppCompatActivity).application)).get(FollowRepoViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_follow_repo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        followersAdapter = UserAdapter()
        followersAdapter.notifyDataSetChanged()

        followingAdapter = UserAdapter()
        followingAdapter.notifyDataSetChanged()

        repositoriesAdapter = RepoAdapter()
        repositoriesAdapter.notifyDataSetChanged()

        ItemSnaperHelper().attachToRecyclerView(rv_user_repo)

        var index = 1
        var username = ""
        if (arguments != null) {
            index = arguments?.getInt(ARG_SECTION_NUMBER, 0) as Int
            username = arguments?.getString(EXTRA_USER) as String
        }

        showLoading(true)

        when (index) {
            1 -> {
                tv_not_found.text = getString(R.string.no_follower_found)
                followRepoViewModel.setListOfFollowers(username, object : VolleyCallBack {
                    override fun onSuccess() {
                        if (rv_user_repo != null && progress_bar != null) {
                            showLoading(false)
                            showFollowersList()
                        }
                    }
                })

                followRepoViewModel.getListOfFollowers().observe(viewLifecycleOwner, Observer { users ->
                    if (users.isNotEmpty()) {
                        followersAdapter.setData(users)
                        tv_not_found.visibility = View.GONE
                        rv_user_repo.visibility = View.VISIBLE
                    } else {
                        showLoading(false)
                        tv_not_found.visibility = View.VISIBLE
                        rv_user_repo.visibility = View.GONE
                    }
                })
            }
            2 -> {
                tv_not_found.text = getString(R.string.no_following_found)
                followRepoViewModel.setListOfFollowing(username, object : VolleyCallBack {
                    override fun onSuccess() {
                        if (rv_user_repo != null && progress_bar != null) {
                            showLoading(false)
                            showFollowingList()
                        }
                    }
                })

                followRepoViewModel.getListOfFollowing().observe(viewLifecycleOwner, Observer { users ->
                    if (users.isNotEmpty()) {
                        followingAdapter.setData(users)
                        tv_not_found.visibility = View.GONE
                        rv_user_repo.visibility = View.VISIBLE
                    } else {
                        showLoading(false)
                        tv_not_found.visibility = View.VISIBLE
                        rv_user_repo.visibility = View.GONE
                    }
                })
            }
            3 -> {
                tv_not_found.text = getString(R.string.no_repository_found)
                followRepoViewModel.setListOfRepositories(username)

                followRepoViewModel.getListOfRepositories().observe(viewLifecycleOwner, Observer { repos ->
                    showLoading(false)
                    if (repos.isNotEmpty()) {
                        repositoriesAdapter.setData(repos)
                        showRepositoriesList()
                        tv_not_found.visibility = View.GONE
                        rv_user_repo.visibility = View.VISIBLE
                    } else {
                        tv_not_found.visibility = View.VISIBLE
                        rv_user_repo.visibility = View.GONE
                    }
                })
            }
            else -> tv_not_found.text = getString(R.string.tab_index_out_of_bounds)
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }


    private fun showFollowersList() {
        rv_user_repo.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,  false)
        rv_user_repo.adapter = followersAdapter

        followersAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
            override fun onItemClicked(user: User) {
                showSelectedUser(user)
            }
        })
    }

    private fun showFollowingList() {
        rv_user_repo.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,  false)
        rv_user_repo.adapter = followingAdapter

        followingAdapter.setOnItemClickCallback(object : UserAdapter.OnItemClickCallback {
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

        val mFragmentManager = parentFragmentManager
        mFragmentManager.beginTransaction().apply {
            detach(mFragmentManager.findFragmentByTag(DetailFragment::class.java.simpleName) as Fragment)
            add(R.id.frame_container, mDetailFragment, DetailFragment::class.java.simpleName)
            addToBackStack("detail")
            commit()
        }
    }

    private fun showRepositoriesList() {
        rv_user_repo.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,  false)
        rv_user_repo.adapter = repositoriesAdapter

        repositoriesAdapter.setOnItemClickCallback(object : RepoAdapter.OnItemClickCallback {
            override fun onItemClicked(repo: Repo) {
                openSelectedRepoInBrowser(repo)
            }
        })
    }

    private fun openSelectedRepoInBrowser(repo: Repo) {
        val uri: Uri = Uri.parse(repo.repoLink)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}