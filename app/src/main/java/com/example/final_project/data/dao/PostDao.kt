package com.example.final_project.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.final_project.data.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY rowid DESC")
    fun observePosts(): LiveData<List<PostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<PostEntity>)

    @Query("DELETE FROM posts WHERE id = :postId")
    suspend fun deletePost(postId: String)

    @Query("DELETE FROM posts")
    suspend fun clearPosts()
}