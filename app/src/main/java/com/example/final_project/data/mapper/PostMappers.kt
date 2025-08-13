package com.example.final_project.data.mapper

import com.example.final_project.data.entity.PostEntity
import com.example.final_project.model.Post


fun Post.toEntity() = PostEntity(
    id = id,
    title = title,
    description = description,
    imageUrl = imageUrl,
    userName = userName,
    comments = comments
)

fun PostEntity.toDomain() = Post(
    id = id,
    title = title,
    description = description,
    imageUrl = imageUrl,
    userName = userName,
    comments = comments
)