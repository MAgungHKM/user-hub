package com.hkm.userhub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import kotlinx.android.synthetic.main.item_user.view.*

class UserAdapter internal constructor(private val listUser: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)

        return UserViewHolder(view)
    }

    override fun getItemCount(): Int = listUser.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = listUser[position]

        holder.bindUser(user)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listUser[holder.adapterPosition])
        }
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindUser(user: User) {
            with(itemView) {
                tv_name.text = user.name
                tv_username.text = user.username
                tv_company.text = user.company
                tv_location.text = user.location
                tv_followers.text = user.followers

                Glide.with(context)
                    .load(user.avatar)
                    .apply(RequestOptions().override(200, 200))
                    .into(img_avatar)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(user: User)
    }
}