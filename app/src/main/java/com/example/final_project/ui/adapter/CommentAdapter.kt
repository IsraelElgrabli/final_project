package com.example.final_project.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project.R
import com.example.final_project.model.Comment

class CommentAdapter : RecyclerView.Adapter<CommentAdapter.CommentVH>() {

    private val items = mutableListOf<Comment>()

    fun submitList(list: List<Comment>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class CommentVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val user: TextView = itemView.findViewById(R.id.textUser)
        private val body: TextView = itemView.findViewById(R.id.textBody)

        fun bind(c: Comment) {
            user.text = c.userName
            body.text = c.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentVH(v)
    }

    override fun onBindViewHolder(holder: CommentVH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
