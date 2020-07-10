package com.hkm.userhub.ui.home

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hkm.userhub.MainActivity.VolleyCallBack
import com.hkm.userhub.NavigationHelper
import com.hkm.userhub.R
import com.hkm.userhub.model.user.User
import com.hkm.userhub.model.user.UserAdapter
import com.hkm.userhub.ui.ItemSnaperHelper
import com.hkm.userhub.ui.detail.DetailFragment
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.progress_bar
import kotlinx.android.synthetic.main.fragment_home.tv_not_found


class HomeFragment : Fragment() {
    private lateinit var userAdapter: UserAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance((activity as AppCompatActivity).application)).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.addOnBackStackChangedListener {
            val topFragment = NavigationHelper.getCurrentTopFragment(parentFragmentManager)
            if (topFragment != null) {
                if (topFragment is HomeFragment) {
                    home_layout.visibility = View.VISIBLE
                }
            }
        }

        userAdapter = UserAdapter()
        userAdapter.notifyDataSetChanged()

        ItemSnaperHelper().attachToRecyclerView(rv_users)

        edt_search.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    edt_search.clearFocus()
                    val key: InputMethodManager? = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    key?.hideSoftInputFromWindow(edt_search.windowToken, 0)
                    tv_not_found.visibility = View.GONE
                    rv_users.visibility = View.GONE
                    showLoading(true)
                    homeViewModel.searchUser(view.text.toString(), object : VolleyCallBack {
                        override fun onSuccess() {
                            if (rv_users != null && progress_bar != null) {
                                showLoading(false)
                                showUserList()
                            }
                        }
                    })
                    return true
                }
                return false
            }
        })

        homeViewModel.getSearchResult().observe(viewLifecycleOwner, Observer { users ->
            if (users.isNotEmpty()) {
                userAdapter.setData(users)
                showUserList()
                tv_not_found.visibility = View.GONE
                rv_users.visibility = View.VISIBLE
            } else {
                showLoading(false)
                tv_not_found.visibility = View.VISIBLE
                rv_users.visibility = View.GONE
            }
        })

        (activity as AppCompatActivity).supportActionBar?.title = "Home"
    }

    private fun showLoading(state: Boolean) {
        if (state)
            progress_bar.visibility = View.VISIBLE
        else
            progress_bar.visibility = View.GONE
    }

    private fun showUserList() {
        rv_users.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL,  false)
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

        val mFragmentManager = parentFragmentManager
        mFragmentManager.beginTransaction().apply {
            add(R.id.frame_container, mDetailFragment, DetailFragment::class.java.simpleName)
            addToBackStack(null)
            commit()
            home_layout.visibility = View.GONE
        }
    }
}