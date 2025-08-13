package com.example.final_project.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project.R
import com.example.final_project.data.repository.PostRepository
import com.example.final_project.ui.adapter.UserPostsAdapter
import com.example.final_project.viewmodal.AuthViewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var adapter: UserPostsAdapter
    private val authVm: AuthViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Guard: if user is not logged in → go to Login
        val currentUser = authVm.currentUsername()
        if (currentUser.isNullOrBlank()) {
            findNavController().navigate(R.id.simpleLoginFragment)
            return
        }

        // Views
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerUserPosts)
        val textUsername = view.findViewById<TextView>(R.id.textUsername)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        textUsername.text = currentUser

        // RecyclerView setup
        adapter = UserPostsAdapter { post ->
            val action = ProfileFragmentDirections
                .actionProfileFragmentToPostDetailFragment(post.id)
            findNavController().navigate(action)
        }
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = adapter

        // Load this user's posts
        val repo = PostRepository(requireContext())
        repo.observePostsByUser(currentUser).observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)
        }

        btnLogout.setOnClickListener {
            authVm.logout() // clear current session (and remembered if you want)

            // Safe Args: pass the flag
            val action = ProfileFragmentDirections
                .actionProfileFragmentToSimpleLoginFragment(fromLogout = true)

            // Also clear the back stack so Back won't return to Profile/Feed
            val navOptions = androidx.navigation.NavOptions.Builder()
                .setPopUpTo(findNavController().graph.startDestinationId, true)
                .build()

            findNavController().navigate(action.actionId, action.arguments, navOptions)
        }

    }
}
