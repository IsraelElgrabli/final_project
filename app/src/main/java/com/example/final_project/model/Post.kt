package com.example.final_project.model

data class Comment(
    val userName: String,
    val text: String
)

data class Post(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val userName: String,
    val comments: List<Comment> = emptyList()
)