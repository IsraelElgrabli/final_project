package com.example.final_project.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project.R
import com.example.final_project.viewmodal.AuthViewModel
import com.example.final_project.ui.adapter.PostAdapter
import com.example.final_project.viewmodal.PostViewModel

class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val vm: PostViewModel by activityViewModels()
    private lateinit var adapter: PostAdapter
    private val authVm: AuthViewModel by activityViewModels()

    private val userName: String
        get() = authVm.currentUsername() ?: "admin"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerFeed)
        val lm = LinearLayoutManager(requireContext())
        lm.reverseLayout = true
        lm.stackFromEnd = true
        recycler.layoutManager = lm

        adapter = PostAdapter(
            items = emptyList(),
            currentUsername = userName,
            onClick = { post ->
                val action = FeedFragmentDirections
                    .actionFeedFragmentToPostDetailFragment(post.id)
                findNavController().navigate(action)
            },
            onEdit = { post -> /* edit */ },
            onDelete = { post -> vm.deletePost(post.id) }
        )
        recycler.adapter = adapter
        recycler.addItemDecoration(DividerItemDecoration(requireContext(), lm.orientation))

        vm.feed.observe(viewLifecycleOwner) { posts ->
            // instead of making a new adapter, just replace the data
            adapter = PostAdapter(
                items = posts,
                currentUsername = userName,
                onClick = { post ->
                    val action = FeedFragmentDirections
                        .actionFeedFragmentToPostDetailFragment(post.id)
                    findNavController().navigate(action)
                },
                onEdit = { post -> /* edit */ },
                onDelete = { post -> vm.deletePost(post.id) }
            )
            recycler.adapter = adapter
        }
    }
}