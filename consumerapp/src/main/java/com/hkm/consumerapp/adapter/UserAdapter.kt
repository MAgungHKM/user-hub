package com.hkm.consumerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hkm.consumerapp.R
import com.hkm.consumerapp.entitiy.User
import kotlinx.android.synthetic.main.item_user.view.*
import java.lang.ref.WeakReference


class UserAdapter :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    private lateinit var onClickCallback: OnClickCallback
    private val listUser = ArrayList<User>()

    fun setOnClickCallback(onClickCallback: OnClickCallback) {
        this.onClickCallback = onClickCallback
    }

    fun setData(users: ArrayList<User>) {
        listUser.clear()
        listUser.addAll(users)
        notifyDataSetChanged()
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
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val onClickWeakCallback: WeakReference<OnClickCallback> =
            WeakReference(onClickCallback)

        fun bindUser(user: User) {
            itemView.setOnClickListener(this)

            with(itemView) {
                tv_username.text = user.username
                tv_followers.text = user.followers

                Glide.with(context)
                    .load(user.avatar)
                    .apply(
                        RequestOptions()
                            .override(200, 200)
                            .placeholder(R.drawable.user_placeholder)
                    )
                    .into(img_avatar)
            }
        }

        override fun onClick(v: View) {
            onClickWeakCallback.get()?.onItemClicked(listUser[adapterPosition])
        }
    }

    interface OnClickCallback {
        fun onItemClicked(user: User)
        fun onDeleteClicked(user: User)
    }
}