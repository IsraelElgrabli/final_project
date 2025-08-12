package com.example.final_project.data.repository



import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.final_project.data.AppDatabase
import com.example.final_project.data.entity.PostEntity
import com.example.final_project.data.mapper.toDomain
import com.example.final_project.data.mapper.toEntity

import com.example.final_project.model.Post
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PostRepository(
    context: Context,
    private val io: CoroutineDispatcher = Dispatchers.IO
) {
    private val dao = AppDatabase.get(context).postDao()
    private val postsCol = Firebase.firestore.collection("posts")
    private var listener: ListenerRegistration? = null

    /** UI observes Room; first call starts Firestore listener that mirrors into Room. */
    fun observeFeed(): LiveData<List<Post>> {
        ensureListener()
        return dao.observePosts().map { list -> list.map { it.toDomain() } }
    }

    /** Add: write to Firestore; on success mirror to Room (instant listener will also catch it). */
    suspend fun insertPost(post: Post) = withContext(io) {
        postsCol.document(post.id).set(post.toFirestoreMap()).await()
        dao.insertPost(post.toEntity())
    }

    /** Delete: write to Firestore; on success you can also delete locally if you expose an API. */
    suspend fun deletePost(postId: String) = withContext(io) {
        postsCol.document(postId).delete().await()
        // Optional: also remove locally if you added a DAO delete:
        // dao.deletePost(postId)
    }

    /** Real-time listener → maps docs → upserts into Room. */
    private fun ensureListener() {
        if (listener != null) return
        listener = postsCol
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val entities = snap.documents.mapNotNull { d -> d.toPostEntityOrNull() }
                GlobalScope.launch(io) { dao.insertPosts(entities) }
            }
    }

    fun stop() {
        listener?.remove()
        listener = null
    }
}

/* ---------- helpers ---------- */

private fun Post.toFirestoreMap() = mapOf(
    "id" to id,
    "title" to title,
    "description" to description,
    "imageUrl" to imageUrl,
    "userName" to userName,
    "createdAt" to Timestamp.now()
)

private fun com.google.firebase.firestore.DocumentSnapshot.toPostEntityOrNull(): PostEntity? {
    val data = data ?: return null
    val id = (data["id"] as? String)?.ifBlank { null } ?: id
    val title = data["title"] as? String ?: return null
    val description = data["description"] as? String ?: ""
    val imageUrl = data["imageUrl"] as? String ?: ""
    val userName = data["userName"] as? String ?: "admin"
    return PostEntity(id, title, description, imageUrl, userName)
}