package com.example.final_project.component

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project.R
import com.example.final_project.model.Comment
import com.example.final_project.model.Post



class FeedFragment : Fragment(R.layout.fragment_feed) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerFeed)
        val layoutManager = LinearLayoutManager(requireContext())
        recycler.layoutManager = layoutManager
        recycler.adapter = PostAdapter(samplePosts())
        recycler.addItemDecoration(DividerItemDecoration(requireContext(), layoutManager.orientation))
    }

    private fun samplePosts(): List<Post> = listOf(
        Post(
            id = "p1",
            title = "Sunrise over Timna",
            description = "First light painting the sandstone pillars. Short hike, huge payoff.",
            imageUrl = "https://images.unsplash.com/photo-1501785888041-af3ef285b470?q=80&w=1200&auto=format&fit=crop",
            userName = "israel",
            comments = listOf(
                Comment("michal", "Wow! Which trail?"),
                Comment("david", "Adding this to my list.")
            )
        ),
        Post(
            id = "p2",
            title = "Jaffa Port Stroll",
            description = "Coffee by the sea and exploring the alleys. Sunset light is unbeatable.",
            imageUrl = "https://images.unsplash.com/photo-1544989164-31dc3c645987?q=80&w=1200&auto=format&fit=crop",
            userName = "yael",
            comments = listOf(Comment("noa", "Love this vibe!"))
        ),
        Post(
            id = "p3",
            title = "Masada Dawn",
            description = "Snake Path at 4:30 AM. Tough climb, history + view = chills.",
            imageUrl = "https://images.unsplash.com/photo-1469474968028-56623f02e42e?q=80&w=1200&auto=format&fit=crop",
            userName = "avi",
            comments = emptyList()
        )
    )
}