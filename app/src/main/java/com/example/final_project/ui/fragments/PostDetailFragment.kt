package com.example.final_project.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project.R
import com.example.final_project.model.Comment
import com.example.final_project.model.Post
import com.example.final_project.ui.adapter.CommentAdapter
import com.example.final_project.viewmodal.AuthViewModel
import com.example.final_project.viewmodal.PostViewModel
import com.squareup.picasso.Picasso

class PostDetailFragment : Fragment(R.layout.fragment_post_detail) {

    private val args: PostDetailFragmentArgs by navArgs()
    private val vm: PostViewModel by activityViewModels()
    private val authVm: AuthViewModel by activityViewModels()

    private val userName: String
        get() = authVm.currentUsername() ?: "admin"

    // Views
    private lateinit var image: ImageView
    private lateinit var title: TextView
    private lateinit var user: TextView
    private lateinit var desc: TextView
    private lateinit var commentsCount: TextView
    private lateinit var editComment: EditText
    private lateinit var btnSendComment: ImageButton
    private lateinit var commentsRecycler: RecyclerView

    // Adapter
    private lateinit var commentsAdapter: CommentAdapter

    private var currentPost: Post? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // find views
        image = view.findViewById(R.id.imagePost)
        title = view.findViewById(R.id.textTitle)
        user = view.findViewById(R.id.textUser)
        desc = view.findViewById(R.id.textDescription)
        commentsCount = view.findViewById(R.id.textComments)
        editComment = view.findViewById(R.id.editComment)
        btnSendComment = view.findViewById(R.id.btnSendComment)
        commentsRecycler = view.findViewById(R.id.recyclerComments)

        // setup comments list
        commentsAdapter = CommentAdapter()

        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true   // newest comment appears first
        layoutManager.stackFromEnd = false   // keep start at top
        commentsRecycler.layoutManager = layoutManager

        commentsRecycler.adapter = commentsAdapter
        commentsRecycler.isNestedScrollingEnabled = false

        // observe the feed and bind the post we care about
        val postId = args.postId
        vm.feed.observe(viewLifecycleOwner) { posts ->
            posts.find { it.id == postId }?.let { post ->
                currentPost = post
                bindPost(post)
            }
        }

        // send a new comment
        btnSendComment.setOnClickListener {
            val text = editComment.text.toString().trim()
            val post = currentPost
            if (text.isNotEmpty() && post != null) {
                val newComment = Comment(userName = userName, text = text)
                val updated = post.copy(comments = post.comments + newComment)
                vm.updatePost(updated)          // Repository persists to Firestore + Room
                editComment.text.clear()
                // UI will refresh via the LiveData observer above
            }
        }
    }

    private fun bindPost(post: Post) {
        title.text = post.title
        user.text = post.userName
        desc.text = post.description

        val count = post.comments.size
        commentsCount.text = if (count == 1) "1 comment" else "$count comments"

        commentsAdapter.submitList(post.comments)

        if (post.imageUrl.isNotBlank()) {
            Picasso.get()
                .load(post.imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .error(R.drawable.ic_image_placeholder)
                .into(image)
        } else {
            image.setImageResource(R.drawable.ic_image_placeholder)
        }
    }
}
