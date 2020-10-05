package com.hkm.consumerapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hkm.consumerapp.R
import com.hkm.consumerapp.entitiy.Repo
import com.hkm.consumerapp.tools.setOnSingleClickListener
import kotlinx.android.synthetic.main.item_repo.view.*

class RepoAdapter :
    RecyclerView.Adapter<RepoAdapter.RepoViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback
    private val listRepo = ArrayList<Repo>()

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setData(repos: ArrayList<Repo>) {
        listRepo.clear()
        listRepo.addAll(repos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_repo, parent, false)

        return RepoViewHolder(view)
    }

    override fun getItemCount(): Int = listRepo.size

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val repo = listRepo[position]

        holder.bindRepo(repo)

        holder.itemView.setOnSingleClickListener {
            onItemClickCallback.onItemClicked(listRepo[holder.adapterPosition])
        }
    }

    inner class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindRepo(repo: Repo) {
            with(itemView) {
                tv_name.text = repo.name
                tv_description.text = repo.description
                tv_stargazers.text = repo.stargazers
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(repo: Repo)
    }
}