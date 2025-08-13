package com.example.final_project.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project.R
import com.example.final_project.model.Post
import com.squareup.picasso.Picasso

class PostAdapter(
    private val items: List<Post>,
    private val currentUsername: String?,
    private val onClick: (Post) -> Unit = {},
    private val onEdit: (Post) -> Unit = {},
    private val onDelete: (Post) -> Unit = {}
) : RecyclerView.Adapter<PostAdapter.PostVH>() {

    inner class PostVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val image: ImageView = itemView.findViewById(R.id.imagePost)
        private val title: TextView = itemView.findViewById(R.id.textTitle)
        private val user: TextView = itemView.findViewById(R.id.textUser)
        private val desc: TextView = itemView.findViewById(R.id.textDescription)
        private val comments: TextView = itemView.findViewById(R.id.textComments)

        private val ownerActions: View = itemView.findViewById(R.id.ownerActions)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(post: Post) {
            title.text = post.title
            user.text = post.userName
            desc.text = post.description
            comments.text = "${post.comments.size} comments"

            if (post.imageUrl.isNotBlank()) {
                Picasso.get()
                    .load(post.imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(image)
            } else {
                image.setImageResource(R.drawable.ic_image_placeholder)
            }

            // Show actions only for the owner
            val mine = post.userName.equals(currentUsername, ignoreCase = true)
            ownerActions.isVisible = mine

            btnDelete.setOnClickListener { onDelete(post) }

            // 🔹 Image opens PostDetail
            image.setOnClickListener { onClick(post) }

            // (Optional) remove whole-card click to avoid double triggers
            itemView.setOnClickListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostVH(v)
    }

    override fun onBindViewHolder(holder: PostVH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
