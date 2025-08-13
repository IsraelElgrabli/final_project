package com.example.final_project.viewmodal

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

    fun getPostsByUser(username: String): LiveData<List<Post>> =
        repo.observePostsByUser(username)

    fun addPost(post: Post) {
        viewModelScope.launch { repo.insertPost(post) }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch { repo.deletePost(postId) }
    }
    fun updatePost(updatedPost: Post) {
        viewModelScope.launch {
            repo.updatePost(updatedPost)
        }
    }
    override fun onCleared() {
        super.onCleared()
        repo.stop()
    }
}