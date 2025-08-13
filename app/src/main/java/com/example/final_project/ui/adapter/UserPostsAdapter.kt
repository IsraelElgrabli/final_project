package com.example.final_project.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project.R
import com.example.final_project.model.Post
import com.squareup.picasso.Picasso

class UserPostsAdapter(
    private val onClick: (Post) -> Unit = {}
) : ListAdapter<Post, UserPostsAdapter.PostVH>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile_post, parent, false)
        return PostVH(view, onClick)
    }

    override fun onBindViewHolder(holder: PostVH, position: Int) {
        holder.bind(getPost(position))
    }

    fun getPost(position: Int): Post = currentList[position]

    class PostVH(
        itemView: View,
        private val onClick: (Post) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val image: ImageView = itemView.findViewById(R.id.imagePost)

        fun bind(post: Post) {
            Picasso.get()
                .load(post.imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .into(image)

            image.setOnClickListener { onClick(post) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(old: Post, new: Post) = old.id == new.id
        override fun areContentsTheSame(old: Post, new: Post) = old == new
    }
}
