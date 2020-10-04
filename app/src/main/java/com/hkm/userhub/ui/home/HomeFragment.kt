package com.hkm.userhub.ui.home

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hkm.userhub.R
import com.hkm.userhub.adapter.UserAdapter
import com.hkm.userhub.entitiy.User
import com.hkm.userhub.tools.ItemSnaperHelper
import com.hkm.userhub.tools.NavigationHelper
import com.hkm.userhub.tools.OnMyFragmentListener
import com.hkm.userhub.ui.MainActivity
import com.hkm.userhub.ui.MainActivity.VolleyCallBack
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment(), Toolbar.OnMenuItemClickListener {
    private lateinit var userAdapter: UserAdapter
    private lateinit var homeViewModel: HomeViewModel
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
        homeViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance((activity as MainActivity).application)
        ).get(HomeViewModel::class.java)
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

        val mFragmentManager = parentFragmentManager
        mFragmentManager.addOnBackStackChangedListener {
            val topFragment = NavigationHelper.getCurrentTopFragment(mFragmentManager)
            if (topFragment != null) {
                if (topFragment is HomeFragment) {
                    if (activity != null) {
                        mOnMyFragmentListener?.onChangeToolbarDisplayHome(false)
                    }
                }
            }
        }

        userAdapter = UserAdapter()
        userAdapter.notifyDataSetChanged()

        ItemSnaperHelper().attachToRecyclerView(rv_users)

        edt_search.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
                ) {
                    edt_search.clearFocus()
                    val key: InputMethodManager? =
                        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                    key?.hideSoftInputFromWindow(edt_search.windowToken, 0)
                    tv_not_found.visibility = View.GONE
                    rv_users.visibility = View.GONE
                    showLoading(true)
                    homeViewModel.searchUser(view.text.toString(), object : VolleyCallBack {
                        override fun onSuccess() {
                            if (rv_users != null && progress_bar != null) {
                                showUserList()
                                showLoading(false)
                            }
                        }
                    })
                    return true
                }
                return false
            }
        })

        homeViewModel.getSearchResult().observe(viewLifecycleOwner, { users ->
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


        homeViewModel.message.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(context, getString(it), Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onResume() {
        super.onResume()
        mOnMyFragmentListener?.onChangeToolbarTitle(getString(R.string.menu_home))
        mOnMyFragmentListener?.setMenuVisibility(R.id.menu_home, false)
        mOnMyFragmentListener?.setMenuVisibility(R.id.menu_delete_all, false)
        mOnMyFragmentListener?.setMenuVisibility(R.id.menu_settings, true)
        mOnMyFragmentListener?.setMenuVisibility(R.id.menu_favorite, true)
        mOnMyFragmentListener?.onOptionsMenuSelected(this)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_home -> {
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
                true
            }
            R.id.menu_favorite -> {
                view?.findNavController()
                    ?.navigate(HomeFragmentDirections.actionHomeFragmentToFavoriteFragment())
                    .apply {
                        mOnMyFragmentListener?.onChangeToolbarTitle(getString(R.string.menu_favorite))
                        mOnMyFragmentListener?.onChangeToolbarDisplayHome(true)
                        mOnMyFragmentListener?.setMenuVisibility(R.id.menu_favorite, false)
                        mOnMyFragmentListener?.setMenuVisibility(R.id.menu_home, false)
                    }
                true
            }
            R.id.menu_settings -> {
                view?.findNavController()
                    ?.navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
                    .apply {
                        mOnMyFragmentListener?.onChangeToolbarTitle(getString(R.string.menu_settings))
                        mOnMyFragmentListener?.onChangeToolbarDisplayHome(true)
                        mOnMyFragmentListener?.setMenuVisibility(R.id.menu_favorite, false)
                        mOnMyFragmentListener?.setMenuVisibility(R.id.menu_home, false)
                        mOnMyFragmentListener?.setMenuVisibility(R.id.menu_delete_all, false)
                        mOnMyFragmentListener?.setMenuVisibility(R.id.menu_settings, false)
                    }
                true
            }
            else -> true
        }
    }

    private fun showLoading(state: Boolean) {
        if (state)
            progress_bar.visibility = View.VISIBLE
        else
            progress_bar.visibility = View.GONE
    }

    private fun showUserList() {
        rv_users.layoutManager =
            LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        rv_users.adapter = userAdapter

        userAdapter.setOnClickCallback(object : UserAdapter.OnClickCallback {
            override fun onItemClicked(user: User) {
                showSelectedUser(user.username)
            }

            override fun onDeleteClicked(user: User) {
                showSelectedUser(user.username)
            }
        })
    }

    private fun showSelectedUser(username: String) {
        val toDetailFragment = HomeFragmentDirections.actionHomeFragmentToDetailFragment()
        toDetailFragment.username = username

        view?.findNavController()?.navigate(toDetailFragment).apply {
            mOnMyFragmentListener?.onChangeToolbarTitle(username)
            mOnMyFragmentListener?.setMenuVisibility(R.id.menu_home, true)
        }
    }

    override fun onDetach() {
        super.onDetach()
        mOnMyFragmentListener = null
    }
}