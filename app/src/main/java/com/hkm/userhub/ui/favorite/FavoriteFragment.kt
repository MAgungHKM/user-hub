package com.hkm.userhub.ui.favorite

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import com.hkm.userhub.tools.OnMyFragmentListener
import com.hkm.userhub.ui.detail.DetailFragment
import kotlinx.android.synthetic.main.fragment_home.*

class FavoriteFragment : Fragment(), Toolbar.OnMenuItemClickListener {
    private lateinit var userAdapter: UserAdapter
    private lateinit var favoriteViewModel: FavoriteViewModel
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
        favoriteViewModel = ViewModelProvider(requireActivity()).get(FavoriteViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoading(true)

        favoriteViewModel.message.observe(viewLifecycleOwner, { event ->
            event.getContentIfNotHandled()?.let {
                Toast.makeText(context, getString(it), Toast.LENGTH_SHORT).show()
            }
        })

        userAdapter = UserAdapter(true)
        userAdapter.notifyDataSetChanged()

        ItemSnaperHelper().attachToRecyclerView(rv_users)

        favoriteViewModel.fetchDataFromRealm()

        favoriteViewModel.getListFavorite().observe(viewLifecycleOwner, { favorites ->
            favorites?.let {
                userAdapter.setData(it)
                showUserList()
                showLoading(false)
            }
        })
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
                favoriteViewModel.deleteFavorite(user.username)
                favoriteViewModel.fetchDataFromRealm()
                favoriteViewModel.getListFavorite().observe(viewLifecycleOwner, { favorites ->
                    favorites?.let {
                        userAdapter.setData(it)
                    }
                })

            }
        })
    }

    private fun showSelectedUser(username: String) {
        val toDetailFragment = FavoriteFragmentDirections.actionFavoriteFragmentToDetailFragment()
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