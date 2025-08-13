// com/example/final_project/data/entity/PostEntity.kt
package com.example.final_project.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.final_project.model.Comment

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val userName: String,
    val comments: List<Comment> = emptyList()
)