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

    @Update
    suspend fun update(post: PostEntity) // ✅ Fixed to use PostEntity

    @Query("DELETE FROM posts")
    suspend fun clearPosts()

    @Query("SELECT * FROM posts WHERE userName = :username ORDER BY rowid DESC")
    fun observePostsByUser(username: String): LiveData<List<PostEntity>>
}
