package com.hkm.userhub.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hkm.userhub.R
import com.hkm.userhub.entitiy.User
import kotlinx.android.synthetic.main.item_user.view.*
import java.lang.ref.WeakReference


class UserAdapter(private val showDivider: Boolean = false) :
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

        holder.showDivider(showDivider)
        holder.bindUser(user)

//        if (showDivider) {
//            holder.bindUser(user)
//            holder.itemView.setOnSingleClickListener {
//                if (it.id == holder.itemView.btn_delete.id)
//                    onClickCallback.onItemClicked(listUser[holder.adapterPosition])
//                else
//                    onClickCallback.onItemClicked(listUser[holder.adapterPosition])
//            }
//        } else {
//            holder.bindUser(user)
//            holder.itemView.setOnSingleClickListener {
//                onClickCallback.onItemClicked(listUser[holder.adapterPosition])
//            }
//        }
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val onClickWeakCallback: WeakReference<OnClickCallback> =
            WeakReference(onClickCallback)

        fun bindUser(user: User) {
            itemView.setOnClickListener(this)
            itemView.btn_delete.setOnClickListener(this)

            with(itemView) {
                tv_username.text = user.username
                tv_followers.text = user.followersCount

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

        fun showDivider(show: Boolean) {
            if (show) {
                with(itemView) {
                    divider.visibility = View.VISIBLE
                }
            } else {
                with(itemView) {
                    divider.visibility = View.GONE
                }
            }
        }

        override fun onClick(v: View) {
            if (v.id == itemView.btn_delete.id) {
                val alert: AlertDialog.Builder = AlertDialog.Builder(v.context, R.style.MyPopupMenu)

                alert.setTitle(v.context.getString(R.string.dial_delete))
                alert.setMessage(v.context.getString(R.string.dial_delete_text))

                alert.setPositiveButton(v.context.getString(R.string.del_confirm_yes)) { _, _ ->
                    onClickWeakCallback.get()?.onDeleteClicked(listUser[adapterPosition])
                }

                alert.setNegativeButton(v.context.getString(R.string.del_confirm_no)) { dialog, _ ->
                    dialog.cancel()
                }

                alert.show()
            } else
                onClickWeakCallback.get()?.onItemClicked(listUser[adapterPosition])
        }
    }

    interface OnClickCallback {
        fun onItemClicked(user: User)
        fun onDeleteClicked(user: User)
    }
}