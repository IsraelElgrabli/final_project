package com.example.final_project.component


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.final_project.data.repository.PostRepository
import com.example.final_project.model.Post
import kotlinx.coroutines.launch

class PostViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = PostRepository(app)

    val feed: LiveData<List<Post>> = repo.observeFeed()

    fun addPost(post: Post) {
        viewModelScope.launch { repo.insertPost(post) }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch { repo.deletePost(postId) }
    }

    override fun onCleared() {
        super.onCleared()
        repo.stop()
    }
}