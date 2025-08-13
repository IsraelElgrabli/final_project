package com.example.final_project.component

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project.R

class FeedFragment : Fragment(R.layout.fragment_feed) {

    private val vm: PostViewModel by activityViewModels()
    private lateinit var adapter: PostAdapter
    private val authVm: AuthSimpleViewModel by activityViewModels()

    private val userName: String
        get() = authVm.currentUsername() ?: "admin"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerFeed)
        val lm = LinearLayoutManager(requireContext())



        recycler.layoutManager = lm
        adapter = PostAdapter(
            items = emptyList(),
            currentUsername = userName,
            onClick = { post -> /* open details */ },
            onEdit = { post -> /* edit */ },
            onDelete = { post ->
                vm.deletePost(post.id) // ViewModel → Repository → Firestore + Room
            }
        )
        recycler.adapter = adapter  // start empty
        recycler.addItemDecoration(DividerItemDecoration(requireContext(), lm.orientation))

        // Observe Room via ViewModel -> update UI
        vm.feed.observe(viewLifecycleOwner) { posts ->
            recycler.adapter = PostAdapter(
                items = posts,
                currentUsername = userName,
                onClick = { /* ... */ },
                onEdit = { /* ... */ },
                onDelete = { post ->
                    vm.deletePost(post.id)
                }
            )
        }
    }
}
