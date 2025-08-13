package com.example.final_project.data.repository

import android.content.Context
import com.example.final_project.auth.PasswordHasher
import com.example.final_project.auth.SessionManager
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore

import kotlinx.coroutines.tasks.await

class AuthRepository(context: Context) {
    private val db = Firebase.firestore
    private val users = db.collection("users")
    private val session = SessionManager(context)

    suspend fun register(username: String, password: String) {
        val uname = username.trim()
        if (uname.isEmpty()) throw IllegalStateException("Username required")
        if (password.isEmpty()) throw IllegalStateException("Password required")

        // check duplicate
        if (users.document(uname).get().await().exists())
            throw IllegalStateException("Username already exists")

        val salt = PasswordHasher.newSalt()
        val hash = PasswordHasher.hash(password, salt)
        users.document(uname).set(
            mapOf(
                "passwordHash" to hash,
                "salt" to salt,
                "createdAt" to Timestamp.now()
            )
        ).await()

        session.username = uname
    }

    suspend fun login(username: String, password: String, remember: Boolean) {
        val uname = username.trim()
        val doc = users.document(uname).get().await()
        if (!doc.exists()) throw IllegalStateException("User not found")

        val salt = doc.getString("salt") ?: throw IllegalStateException("User record invalid")
        val stored = doc.getString("passwordHash") ?: throw IllegalStateException("User record invalid")
        val computed = PasswordHasher.hash(password, salt)
        if (computed != stored) throw IllegalStateException("Wrong password")

        session.username = uname
        session.rememberedUsername = if (remember) uname else null
    }

    fun logout() = session.clear()
    fun currentUsername(): String? = session.username
    fun rememberedUsername(): String? = session.rememberedUsername
}