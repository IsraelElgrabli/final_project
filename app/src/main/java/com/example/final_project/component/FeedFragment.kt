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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerFeed)
        val lm = LinearLayoutManager(requireContext())
        recycler.layoutManager = lm
        adapter = PostAdapter(emptyList())   // start empty
        recycler.adapter = adapter
        recycler.addItemDecoration(DividerItemDecoration(requireContext(), lm.orientation))

        // Observe Room via ViewModel -> update UI
        vm.feed.observe(viewLifecycleOwner) { posts ->
            // If your PostAdapter is a simple adapter that takes a list in ctor:
            recycler.adapter = PostAdapter(posts)

            // If you converted to ListAdapter, use:
            // adapter.submitList(posts)
        }
    }
}
